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
import connectors.{CitizensDetailsConnector, DataCacheConnector, SaConnector, TaxEnrolmentsConnector}
import controllers.Assets.{InternalServerError, Redirect}
import controllers.sa.{routes => saRoutes}
import handlers.ErrorHandler
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{IvLinks, YourSaIsNotInThisAccount}
import play.api.Logging
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SaService @Inject()(saConnector: SaConnector,
                          dataCacheConnector: DataCacheConnector,
                          citizensDetailsConnector: CitizensDetailsConnector,
                          taxEnrolmentsConnector: TaxEnrolmentsConnector,
                          errorHandler: ErrorHandler,
                          appConfig: FrontendAppConfig) extends Logging {

  val serviceUrl: String = appConfig.identityVerificationHost

  def getIvRedirectLink(utr: String, origin: String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[String] = {

    saConnector.getIvLinks(utr, origin).map {
      case Some(ivLinks) =>
        dataCacheConnector.save[IvLinks](request.request.credId, "IvLinksId", ivLinks)
        s"${serviceUrl}${ivLinks.link}"
      case _ =>
        logger.error("[SaService][getIvRedirectLink] Failed retrieving IV link from SA")
        saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), origin).url
    }
  }

  def yourSaIsNotInThisAccount(yourSaIsNotInThisAccount: YourSaIsNotInThisAccount, origin: String): Future[Result] = {
    yourSaIsNotInThisAccount match {
      case YourSaIsNotInThisAccount.LookInOtherAccount =>
        Future.successful(Redirect(Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials"))))
      case YourSaIsNotInThisAccount.AddToThisAccount   =>
        Future.successful(Redirect(saRoutes.SelectSACategoryController.onPageLoadHasUTR(Some(origin))))
    }
  }

  def checkCIDNinoComparison(origin: String, utr: String)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Option[String]] = {
    if(request.request.confidenceLevel.level >= 200) {
      val accountNino: String = request.request.nino match {
        case Some(nino) => nino
        case _           => ""
      }
      citizensDetailsConnector.getDesignatoryDetails("IR-SA", utr).flatMap {
        case Some(cidDetails) =>
          if (cidDetails.nino == accountNino) {
            for {
              enrolForSaBoolean <- taxEnrolmentsConnector.enrolForSa(utr, request.request.credId, request.request.groupId, "enrolAndActivate")
            } yield {
              if (enrolForSaBoolean) {
                logger.info("[SaService][checkCIDNinoComparison] Success through CID checks and enrolled")
                Some(saRoutes.EnrolmentSuccessController.onPageLoad(origin).url)
              } else {
                logger.info("[SaService][checkCIDNinoComparison] Success through CID checks and try pin")
                Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin).url)
              }
            }
          } else {
            Future.successful(Some(saRoutes.RetryKnownFactsController.onPageLoad(origin).url))
          }
        case _ =>
          logger.warn("[SaService][checkCIDNinoComparison] Error Retrieving CID details")
          Future.successful(Some("CidError"))
      }
    } else {
      Future.successful(None)
    }
  }

}
