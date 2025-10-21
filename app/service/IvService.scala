/*
 * Copyright 2024 HM Revenue & Customs
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

import connectors.{CitizensDetailsConnector, DataCacheConnector, IvConnector, TaxEnrolmentsConnector}
import controllers.sa.{routes => saRoutes}
import identifiers.EnterSAUTRId
import models.requests.ServiceInfoRequest
import models.sa.{IvLinks, SAUTR, SaEnrolmentDetails}
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result}
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggingUtil
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IvService @Inject()(dataCacheConnector: DataCacheConnector,
                          ivConnector: IvConnector,
                          taxEnrolmentsConnector: TaxEnrolmentsConnector,
                          citizensDetailsConnector: CitizensDetailsConnector) extends LoggingUtil {

  def journeyLinkCheckUplift(journeyId: String)
                            (implicit ec: ExecutionContext, hc: HeaderCarrier, request: ServiceInfoRequest[AnyContent]): Future[String] = {
    ivConnector.checkJourneyLinkUplift(journeyId).map(_.result).recover {
      case exception =>
        errorLog(s"[IvService][journeyLinkCheckUplift] journeyId: $journeyId" +
          s"\n Check journey link failed with ${exception.getMessage}")
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
          errorLog(s"[IvService][journeyLinkCheck] Check journey link failed with ${exception.getMessage}")
          "Failed"
      }
      case _ =>
        warnLog("[IvService][journeyLinkCheck] Failed to retrieve IvLinks from DataCache")
        Future.successful("Failed")
    }
  }

  def ivCheckAndEnrolUplift(origin: String, journeyId: String)(implicit request: ServiceInfoRequest[AnyContent],
                                                               ec: ExecutionContext,
                                                               hc: HeaderCarrier): Future[Result] = {
    journeyLinkCheckUplift(journeyId).flatMap {
      case "Success" =>
        val accountNino: String = request.request.nino.getOrElse("").toLowerCase
        infoLog(s"[IvService][ivCheckAndEnrolUplift] success response received from journeyLinkCheckUplift" +
          s"\n journeyId: $journeyId" +
          s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
          s"\n accountNino: $accountNino" +
          s"\n confidenceLevel ${request.request.confidenceLevel}"
        )
        for {
          maybeSAUTR: Option[SAUTR] <- dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
          enrolForSaStatus <- {
            maybeSAUTR.map { utr =>
              citizensDetailsConnector.getDesignatoryDetailsForKnownFacts("IR-SA", utr.value).flatMap {
                case Some(details) =>
                  if (details.nino.toLowerCase == accountNino.toLowerCase) {
                    infoLog(s"[IvService][ivCheckAndEnrolUplift] attempting to enrol for SA" +
                      s"\n journeyId: $journeyId" +
                      s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
                      s"\n utr: ${utr.value}" +
                      s"\n accountNino: $accountNino" +
                      s"\n utr-nino: ${details.nino}" +
                      s"\n confidenceLevel ${request.request.confidenceLevel}"
                    )
                    taxEnrolmentsConnector.enrolForSa(utr.value, "enrolAndActivate").flatMap{
                      case true =>  Future.successful("success")
                      case _ =>     Future.successful("failed")
                    }
                  } else {
                    infoLog(s"[IvService][ivCheckAndEnrolUplift] account NINO and UTR NINO is not matching" +
                      s"\n journeyId: $journeyId" +
                      s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
                      s"\n utr: ${utr.value}" +
                      s"\n accountNino: $accountNino" +
                      s"\n utr-nino: ${details.nino}" +
                      s"\n confidenceLevel ${request.request.confidenceLevel}"
                    )
                    Future.successful("ninomismatch")
                  }
              case _ =>
                infoLog(s"[IvService][ivCheckAndEnrolUplift] unable to retrive NINO for UTR " +
                  s"\n journeyId: $journeyId" +
                  s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
                  s"\n utr: ${utr.value}" +
                  s"\n accountNino: $accountNino" +
                  s"\n confidenceLevel ${request.request.confidenceLevel}"
                )
                Future.successful("failed")
              }
            }.getOrElse {
              infoLog(s"[IvService][ivCheckAndEnrolUplift] could not retrieve SAUTR from data cache" +
                s"\n journeyId: $journeyId" +
                s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
                s"\n confidenceLevel ${request.request.confidenceLevel}"
              )
              Future.successful("failed")
            }
          }
        } yield {
          if(enrolForSaStatus == "success") {
            infoLog(s"[IvService][ivCheckAndEnrolUplift] successfully enrolled for SA" +
              s"\n journeyId: $journeyId" +
              s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
              s"\n confidenceLevel ${request.request.confidenceLevel}"
            )
            Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(origin))
          } else if(enrolForSaStatus == "ninomismatch") {
            infoLog(s"[IvService][ivCheckAndEnrolUplift] account NINO and UTR NINO is not matching. Redirecting to RetryKnownFactsController" +
              s"\n journeyId: $journeyId" +
              s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
              s"\n confidenceLevel ${request.request.confidenceLevel}"
            )
            Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin))
          } else {
            infoLog(s"[IvService][ivCheckAndEnrolUplift] enrolment unsuccessful. Redirecting to TryPinInPost" +
              s"\n journeyId: $journeyId" +
              s"\n enrolments ${request.request.enrolments.enrolments.map(_.key)} " +
              s"\n confidenceLevel ${request.request.confidenceLevel}"
            )
            Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin))
          }
        }
      case "LockedOut" =>
        warnLog(s"[IvService][ivCheckAndEnrol] journeyId: $journeyId LockedOut")
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin)))
      case x if x == "InsufficientEvidence" || x == "PreconditionFailed" || x == "FailedMatching" || x == "FailedIV" =>
        warnLog(s"[IvService][ivCheckAndEnrol]journeyId: $journeyId $x")
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), origin)))
      case result =>
        warnLog(s"[IvService][ivCheckAndEnrol] journeyId: $journeyId Failed Iv for $result")
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
              taxEnrolmentsConnector.enrolForSa(utr.value, "enrolAndActivate")
            }.getOrElse(Future.successful(false))
          }
        } yield {
          if (enrolForSaBoolean) {
            Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(origin))
          } else {
            Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin))
          }
        }
      case "LockedOut" => Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin)))
      case x if x == "InsufficientEvidence" || x == "PreconditionFailed" || x == "FailedMatching" || x == "FailedIV" =>
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), origin)))
      case result =>
        warnLog(s"[IvService][ivCheckAndEnrol] Failed Iv for $result")
        Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin)))
    }
  }

  def journeyRouter(details: SaEnrolmentDetails)(implicit request: Request[_]): Future[String] = {
    details match {
      case SaEnrolmentDetails(Some(utr), origin, credId) =>
        infoLog(s"[IvService][journeyRouter] - Successfully saved")
        dataCacheConnector.save[SAUTR](credId, EnterSAUTRId.toString, SAUTR(utr))
        Future.successful(controllers.sa.routes.TryIvController.onPageLoad(origin).url)

      case SaEnrolmentDetails(None, origin, credId) =>
        warnLog(s"[IvService][journeyRouter] - Failed to save with no utr")
        dataCacheConnector.save[Boolean](credId, "tryAgain", true)
        Future.successful(controllers.sa.routes.EnterSAUTRController.onPageLoad(Some(origin)).url)

    }
  }

}
