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

import connectors.{DataCacheConnector, IvConnector, TaxEnrolmentsConnector}
import controllers.sa.{routes => saRoutes}
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{IvLinks, SAUTR, SaEnrolmentDetails}
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class IvService @Inject()(dataCacheConnector: DataCacheConnector,
                         ivConnector: IvConnector,
                         taxEnrolmentsConnector: TaxEnrolmentsConnector) extends Logging {

  def journeyLinkCheckUplift(journeyId: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[String] = {
        ivConnector.checkJourneyLinkUplift(journeyId).map(_.result).recover {
        case exception =>
          logger.error(s"[IvService][journeyLinkCheckUplift] Check journey link failed with ${exception.getMessage}")
          "Failed"
      }
  }

  def journeyLinkCheck()(implicit request: ServiceInfoRequest[AnyContent],
                         ec: ExecutionContext,
                         hc: HeaderCarrier): Future[String] = {

    val journeyLink: Future[Option[IvLinks]] = dataCacheConnector.getEntry[IvLinks](request.request.credId, "IvLinksId")

    journeyLink.flatMap {
      case Some(x) => ivConnector.checkJourneyLink(x.journeyLink).map { result =>
        result.result
      }.recover {
        case exception =>
          logger.error(s"[IvService][journeyLinkCheck] Check journey link failed with ${exception.getMessage}")
          "Failed"
      }
      case _ =>
        logger.warn("[IvService][journeyLinkCheck] Failed to retrieve IvLinks from DataCache")
        Future.successful("Failed")
    }
  }

  def ivCheckAndEnrolUplift(origin: String, journeyId: String)(implicit request: ServiceInfoRequest[AnyContent],
                                      ec: ExecutionContext,
                                      hc: HeaderCarrier): Future[Result] = {
    journeyLinkCheckUplift(journeyId).flatMap {
      case "Success" =>
        for {
          maybeSAUTR <- dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
          enrolForSaBoolean <- {
            maybeSAUTR.map { utr =>
              taxEnrolmentsConnector.enrolForSa(utr.value, request.request.credId, request.request.groupId, "enrolAndActivate")
            }.getOrElse(Future.successful(false))
          }
        } yield {
          if(enrolForSaBoolean) {
            Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(origin))
          } else {
            Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin))
          }
        }
      case "LockedOut" => Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin)))
      case x if x == "InsufficientEvidence" || x == "PreconditionFailed" || x == "FailedMatching" || x == "FailedIV" =>
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), origin)))
      case result =>
        logger.warn(s"[IvService][ivCheckAndEnrol] Failed Iv for ${result}")
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin)))
    }
  }

  def ivCheckAndEnrol(origin: String)(implicit request: ServiceInfoRequest[AnyContent],
                        ec: ExecutionContext,
                        hc: HeaderCarrier): Future[Result] = {
    journeyLinkCheck().flatMap {
      case "Success" =>
        for {
          maybeSAUTR <- dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
          enrolForSaBoolean <- {
            maybeSAUTR.map { utr =>
              taxEnrolmentsConnector.enrolForSa(utr.value, request.request.credId, request.request.groupId, "enrolAndActivate")
            }.getOrElse(Future.successful(false))
          }
          } yield {
            if(enrolForSaBoolean) {
              Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(origin))
            } else {
              Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin))
            }
          }
      case "LockedOut" => Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin)))
      case x if x == "InsufficientEvidence" || x == "PreconditionFailed" || x == "FailedMatching" || x == "FailedIV" =>
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), origin)))
      case result =>
        logger.warn(s"[IvService][ivCheckAndEnrol] Failed Iv for ${result}")
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin)))
    }
  }

  def journeyRouter(details: SaEnrolmentDetails): Future[String] = {
    details match {
      case SaEnrolmentDetails(Some(utr), origin, credId) => {
        dataCacheConnector.save[SAUTR](credId, EnterSAUTRId.toString, SAUTR(utr))
        Future.successful(controllers.sa.routes.TryIvController.onPageLoad(origin).url)
      }
      case SaEnrolmentDetails(None, origin, credId) => {
        dataCacheConnector.save[Boolean](credId, "tryAgain", true)
        Future.successful(controllers.sa.routes.EnterSAUTRController.onPageLoad(Some(origin)).url)
      }
    }
  }

}
