/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package service

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.IvUpliftSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import connectors.{CitizensDetailsConnector, DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.sa.{routes => saRoutes}
import handlers.ErrorHandler
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa._
import utils.LoggingUtil
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsService @Inject()(saService: SaService,
                                  dataCacheConnector: DataCacheConnector,
                                  enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                  auditService: AuditService,
                                  errorHandler: ErrorHandler,
                                  citizensDetailsConnector: CitizensDetailsConnector,
                                  implicit val appConfig: FrontendAppConfig) extends LoggingUtil {

  def knownFactsLocation(knownFacts: KnownFacts,
                         origin: String)
                        (implicit request: ServiceInfoRequest[AnyContent],
                         ec: ExecutionContext,
                         hc: HeaderCarrier): Result = {
    val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
    val queryKnownFactsResult: Future[KnownFactsReturn] = utr.flatMap {
      maybeSAUTR => (
          for {
            utr <- maybeSAUTR
          } yield enrolmentStoreProxyConnector.queryKnownFacts(utr, knownFacts)
        ).getOrElse(Future.successful(KnownFactsReturn("", knownFactsResult = false)))
      }

    queryKnownFactsResult.flatMap {
      case result@KnownFactsReturn(utr, true) if(request.request.nino.isDefined) => {
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        checkCIDNinoComparison(origin, utr, knownFacts.nino.getOrElse(""))
      }
      case result@KnownFactsReturn(_, true) if(isEnabled(IvUpliftSwitch)) =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        Redirect(appConfig.ivUpliftUrl(origin))
      case result@KnownFactsReturn(_, true) =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        saService.getIvRedirectLink(result.utr, origin).map(link => Redirect(Call("GET", link)))
      case result =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = false)
        Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin))
    }
  }

  def enrolmentCheck(credId: String,
                     saUTR: SAUTR,
                     groupId: String,
                     saEnrolment: Option[String],
                     doYouHaveSaUtr: DoYouHaveSAUTR)(implicit request: ServiceInfoRequest[AnyContent],
                                                   ec: ExecutionContext,
                                                   hc: HeaderCarrier): Future[EnrolmentCheckResult] = {

   val enrolmentCheck: Future[EnrolmentCheckResult] = {
     if(doYouHaveSaUtr.equals(DoYouHaveSAUTR.Yes) && saEnrolment.nonEmpty) {
       val saEnrolmentString = saEnrolment.getOrElse("")
       enrolmentStoreProxyConnector.checkExistingUTR(saUTR.value, saEnrolmentString).flatMap { enrolmentStoreResult =>
         if (!enrolmentStoreResult) {
           enrolmentStoreProxyConnector.checkSaGroup(groupId, saEnrolmentString).map(
             res => if (res) GroupIdFound else NoRecordFound
           )
         } else { Future.successful(CredIdFound) }
       }
     } else { Future.successful(NoSaUtr) }
   }

    enrolmentCheck.map { enrolmentCheckResult =>
      auditService.auditSA(credId, saUTR.value, enrolmentCheckResult)
    }

    enrolmentCheck
  }

  def checkCIDNinoComparison(origin: String, utr: String, knownFactsNino: String)
  (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Result= {
    val accountNino: String = request.request.nino.getOrElse("").toLowerCase

    if (accountNino == knownFactsNino.replaceAll("\\s+", "").toLowerCase) {
      citizensDetailsConnector.getDesignatoryDetailsForKnownFacts("IR-SA", utr).map {
        case Some(cidDetails) =>
          if (cidDetails.nino.toLowerCase == accountNino) {
            Redirect(appConfig.ivUpliftUrl(origin))
          } else {
            Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin))
          }
        case None =>
          warnLog("[KnownFactsService][checkCIDNinoComparison] Error Retrieving CID details. Empty data received from Citizens service")
          Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin))
      }.recover {
        case nfe: NotFoundException =>
          warnLog(s"[KnownFactsService][checkCIDNinoComparison] ${nfe.getMessage}")
          Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin))

        case e: Exception =>
          errorLog(s"[KnownFactsService][checkCIDNinoComparison] Error Retrieving CID details ${e.getMessage}")
          Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin))
      }
    } else {
      warnLog(s"[KnownFactsService][checkCIDNinoComparison] Account authorization NINO and knownFactsNINO are not the same")
      Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin))

    }
  }
}
