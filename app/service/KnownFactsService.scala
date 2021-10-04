/*
 * Copyright 2021 HM Revenue & Customs
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
import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector, IvConnector}
import controllers.Assets.Redirect
import controllers.sa.{routes => saRoutes}
import identifiers.EnterSAUTRId
import models.requests.ServiceInfoRequest
import models.sa._
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsService @Inject()(saService: SaService,
                                  dataCacheConnector: DataCacheConnector,
                                  enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                  auditService: AuditService,
                                  appConfig: FrontendAppConfig){

  def knownFactsLocation(knownFacts: KnownFacts,
                         origin: String)
                        (implicit request: ServiceInfoRequest[AnyContent],
                         ec: ExecutionContext,
                         hc: HeaderCarrier): Future[Result] = {
    val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
    val queryKnownFactsResult: Future[KnownFactsReturn] = utr.flatMap {
      maybeSAUTR => (
          for {
            utr <- maybeSAUTR
          } yield enrolmentStoreProxyConnector.queryKnownFacts(utr, knownFacts)
        ).getOrElse(Future.successful(KnownFactsReturn("", knownFactsResult = false)))
      }

    queryKnownFactsResult.flatMap {
      case result@KnownFactsReturn(_, true) if(appConfig.ivUpliftFeatureSwitch) =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        Future.successful(Redirect(appConfig.ivUpliftUrl(origin)))
      case result@KnownFactsReturn(_, true) =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        saService.getIvRedirectLink(result.utr, origin).map(link => Redirect(Call("GET", link)))
      case result =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = false)
        Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin)))
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
}
