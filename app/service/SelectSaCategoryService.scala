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
import connectors.DataCacheConnector
import controllers.Assets.{InternalServerError, Redirect}
import controllers.sa.partnership.{routes => saPartnerRoutes}
import controllers.sa.trust.{routes => trustRoutes}
import controllers.sa.{routes => saRoutes}
import handlers.ErrorHandler
import identifiers.EnterSAUTRId
import models.requests.ServiceInfoRequest
import models.sa._
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.http.HeaderCarrier
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class SelectSaCategoryService @Inject()(dataCacheConnector: DataCacheConnector,
                                        knownFactsService: KnownFactsService,
                                        saService: SaService,
                                        appConfig: FrontendAppConfig,
                                        errorHandler: ErrorHandler,
                                        auditService: AuditService) {

  val accessMtdFeatureSwitch: Boolean = appConfig.accessMtdFeatureSwitch

  def saCategoryResult(saType: SelectSACategory,
                       doYouHaveSaUtr: DoYouHaveSAUTR,
                       origin: String)(implicit request: ServiceInfoRequest[AnyContent],
                                       ec: ExecutionContext,
                                       hc: HeaderCarrier): Future[Result] = {

    val saEnrolment: Option[String] = saType match {
      case SelectSACategory.Sa => Some("IR-SA")
      case SelectSACategory.Partnership => Some("IR-SA-PART-ORG")
      case SelectSACategory.Trust => Some("IR-SA-TRUST-ORG")
      case _ => None
    }

    for {
      utr <- dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString).map(_.getOrElse(SAUTR("")))
      enrolmentStoreResult <- knownFactsService.enrolmentCheck(request.request.credId, utr, request.request.groupId, saEnrolment, doYouHaveSaUtr)
      checkNinoResult <- saService.checkCIDNinoComparison(origin, utr.value)
    } yield {
      if (accessMtdFeatureSwitch) {
        auditService.auditSelectSACategory(saType, doYouHaveSaUtr, utr.value, request.request.credId, request.request.groupId)
      }
        saType match {
          case SelectSACategory.Sa => saResult(doYouHaveSaUtr, enrolmentStoreResult, origin, checkNinoResult)
          case SelectSACategory.Partnership => partnershipResult(doYouHaveSaUtr, enrolmentStoreResult)
          case SelectSACategory.Trust => trustsResult(doYouHaveSaUtr, enrolmentStoreResult)
          case SelectSACategory.MtdIT => {
            Redirect(Call(method = "GET", url = appConfig.mtdItUrl))
          }
        }
      }
    }

  private def saResult(doYouHaveSaUtr: DoYouHaveSAUTR,
                       enrolmentStoreResult: EnrolmentCheckResult,
                       origin: String,
                       ninoCheckResult: Option[String])
                       (implicit request: ServiceInfoRequest[AnyContent]): Result = {
    (doYouHaveSaUtr, enrolmentStoreResult, ninoCheckResult) match {
      case (_, _, Some(result)) => if(result.contains("CidError")) {
        InternalServerError(errorHandler.internalServerErrorTemplate)
      } else { Redirect(Call("GET", result))}
      case (DoYouHaveSAUTR.Yes, NoRecordFound, _) => Redirect(saRoutes.KnownFactsController.onPageLoad(origin))
      case (_, CredIdFound, _)                    => Redirect(Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials")))
      case (_, GroupIdFound, _)                   => Redirect(saRoutes.GroupIdFoundController.onPageLoad())
      case (_, _, _) if (request.request.affinityGroup.contains(AffinityGroup.Individual)) =>
        Redirect(saRoutes.AreYouSelfEmployedController.onPageLoad())
      case (_, _, _)                              => Redirect(Call("GET", appConfig.getPortalUrl("selectTaxes")))
    }
  }

  private def partnershipResult(doYouHaveSaUtr: DoYouHaveSAUTR,
                                enrolmentStoreResult: EnrolmentCheckResult)
                                (implicit request: ServiceInfoRequest[AnyContent]): Result = {
    (doYouHaveSaUtr, enrolmentStoreResult) match {
      case (_, CredIdFound)     => Redirect(Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials")))
      case (_, GroupIdFound)    => Redirect(saRoutes.GroupIdFoundController.onPageLoad())
      case (_, _) if (request.request.affinityGroup.contains(AffinityGroup.Organisation)) =>
        Redirect(saPartnerRoutes.DoYouWantToAddPartnerController.onPageLoad())
      case (_, _)    => Redirect(saPartnerRoutes.SetUpNewAccountController.onPageLoad())
    }
  }

  private def trustsResult(doYouHaveSaUtr: DoYouHaveSAUTR,
                           enrolmentStoreResult: EnrolmentCheckResult)
                           (implicit request: ServiceInfoRequest[AnyContent]): Result = {
    (doYouHaveSaUtr, enrolmentStoreResult) match {
      case (_, CredIdFound)    => Redirect(Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials")))
      case (_, GroupIdFound)   => Redirect(saRoutes.GroupIdFoundController.onPageLoad())
      case (_, _) if (request.request.affinityGroup.contains(AffinityGroup.Organisation)) =>
        Redirect(trustRoutes.HaveYouRegisteredTrustController.onPageLoad())
      case (_, _) => Redirect(trustRoutes.SetUpNewAccountController.onPageLoad())
    }
  }

}


