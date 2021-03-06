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
import controllers.Assets.Redirect
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{CredIdFound, DoYouHaveSAUTR, EnrolmentCheckResult, GroupIdFound, NoRecordFound, SAUTR, SelectSACategory}
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier
import controllers.sa.{routes => saRoutes}
import controllers.sa.partnership.{routes => saPartnerRoutes}
import controllers.sa.trust.{routes => trustRoutes}
import uk.gov.hmrc.auth.core.AffinityGroup

import scala.concurrent.{ExecutionContext, Future}

class SelectSaCategoryService @Inject()(dataCacheConnector: DataCacheConnector,
                                        knownFactsService: KnownFactsService,
                                        appConfig: FrontendAppConfig) {

  def saCategoryResult(saType: SelectSACategory,
                       doYouHaveSaUtr: DoYouHaveSAUTR,
                       origin: String)(implicit request: ServiceInfoRequest[AnyContent],
                                                                          ec: ExecutionContext,
                                                                          hc: HeaderCarrier): Future[Result] = {
    val saEnrolment: String = saType match {
      case SelectSACategory.Sa          => "IR-SA"
      case SelectSACategory.Partnership => "IR-SA-PART-ORG"
      case SelectSACategory.Trust       => "IR-SA-TRUST-ORG"
    }


    for {
      utr <- dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString).map(_.getOrElse(SAUTR("")))
      enrolmentStoreResult <- knownFactsService.enrolmentCheck(request.request.credId, utr, request.request.groupId, saEnrolment, doYouHaveSaUtr)
    } yield {
      saType match {
        case SelectSACategory.Sa          => saResult(doYouHaveSaUtr, enrolmentStoreResult, origin)
        case SelectSACategory.Partnership => partnershipResult(doYouHaveSaUtr, enrolmentStoreResult)
        case SelectSACategory.Trust       => trustsResult(doYouHaveSaUtr, enrolmentStoreResult)
      }
    }
  }

  def saResult(doYouHaveSaUtr: DoYouHaveSAUTR,
               enrolmentStoreResult: EnrolmentCheckResult,
               origin: String)
                (implicit request: ServiceInfoRequest[AnyContent]): Result = {
    (doYouHaveSaUtr, enrolmentStoreResult) match {
      case (DoYouHaveSAUTR.Yes, NoRecordFound) => Redirect(saRoutes.KnownFactsController.onPageLoad(origin))
      case (_, CredIdFound)                    => Redirect(Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials")))
      case (_, GroupIdFound)                   => Redirect(saRoutes.GroupIdFoundController.onPageLoad())
      case (_, _) if (request.request.affinityGroup.contains(AffinityGroup.Individual)) =>
        Redirect(saRoutes.AreYouSelfEmployedController.onPageLoad())
      case (_, _)                              => Redirect(Call("GET", appConfig.getPortalUrl("selectTaxes")))
    }
  }

  def partnershipResult(doYouHaveSaUtr: DoYouHaveSAUTR,
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

  def trustsResult(doYouHaveSaUtr: DoYouHaveSAUTR,
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


