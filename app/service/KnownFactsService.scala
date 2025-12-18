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

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.{IvUpliftSwitch, RealVatEtmpCheck, VATKnownFactsCheck}
import config.featureToggles.FeatureToggleSupport.isEnabled
import connectors.{CitizensDetailsConnector, DataCacheConnector, EnrolmentStoreProxyConnector, VatSubscriptionConnector}
import controllers.sa.{routes => saRoutes}
import controllers.vat.{routes => vatRoutes}
import handlers.ErrorHandler
import identifiers.EnterSAUTRId
import models.requests.ServiceInfoRequest
import models.sa._
import play.api.http.Status.{NOT_FOUND, OK, PRECONDITION_FAILED}
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}
import utils.{LoggingUtil, Navigator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

//noinspection ScalaStyle
class KnownFactsService @Inject() (saService: SaService,
                                   dataCacheConnector: DataCacheConnector,
                                   enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                   vatSubscriptionConnector: VatSubscriptionConnector,
                                   auditService: AuditService,
                                   errorHandler: ErrorHandler,
                                   citizensDetailsConnector: CitizensDetailsConnector,
                                   navigator: Navigator[Call],
                                   implicit val appConfig: FrontendAppConfig)
    extends LoggingUtil {

  def knownFactsLocation(knownFacts: KnownFacts, origin: String)(implicit
      request: ServiceInfoRequest[AnyContent],
      ec: ExecutionContext,
      hc: HeaderCarrier): Future[Result] = {
    val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
    val queryKnownFactsResult: Future[KnownFactsReturn] = utr.flatMap { maybeSAUTR =>
      (
        for {
          utr <- maybeSAUTR
        } yield enrolmentStoreProxyConnector.queryKnownFacts(utr, knownFacts)
      ).getOrElse(Future.successful(KnownFactsReturn("", knownFactsResult = false)))
    }

    queryKnownFactsResult.flatMap {
      case result @ KnownFactsReturn(utr, true) if request.request.nino.isDefined =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        checkCIDNinoComparison(origin, utr, knownFacts.nino.getOrElse(""))
      case result @ KnownFactsReturn(_, true) if isEnabled(IvUpliftSwitch) =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        Future.successful(Redirect(appConfig.ivUpliftUrl(origin)))
      case result @ KnownFactsReturn(_, true) =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = true)
        saService.getIvRedirectLink(result.utr, origin).map(link => Redirect(Call("GET", link)))
      case result =>
        auditService.auditSAKnownFacts(request.request.credId, result.utr, knownFacts, knownfactsResult = false)
        Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin)))
    }
  }

  def enrolmentCheck(credId: String, saUTR: SAUTR, groupId: String, saEnrolment: Option[String], doYouHaveSaUtr: DoYouHaveSAUTR)(implicit
      request: ServiceInfoRequest[AnyContent],
      ec: ExecutionContext,
      hc: HeaderCarrier): Future[EnrolmentCheckResult] = {

    val enrolmentCheck: Future[EnrolmentCheckResult] =
      if (doYouHaveSaUtr.equals(DoYouHaveSAUTR.Yes) && saEnrolment.nonEmpty) {
        val saEnrolmentString = saEnrolment.getOrElse("")
        enrolmentStoreProxyConnector.checkExistingUTR(saUTR.value, saEnrolmentString).flatMap { enrolmentStoreResult =>
          if (!enrolmentStoreResult) {
            enrolmentStoreProxyConnector.checkSaGroup(groupId, saEnrolmentString).map(res => if (res) GroupIdFound else NoRecordFound)
          } else { Future.successful(CredIdFound) }
        }
      } else { Future.successful(NoSaUtr) }

    enrolmentCheck.flatMap { enrolmentCheckResult =>
      auditService.auditSA(credId, saUTR.value, enrolmentCheckResult).map(_ => enrolmentCheckResult)
    }
  }

  def checkCIDNinoComparison(origin: String, utr: String, knownFactsNino: String)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      request: ServiceInfoRequest[AnyContent]): Future[Result] = {
    val accountNino: String = request.request.nino.getOrElse("").toLowerCase

    if (accountNino == knownFactsNino.replaceAll("\\s+", "").toLowerCase) {
      citizensDetailsConnector
        .getDesignatoryDetailsForKnownFacts("IR-SA", utr)
        .flatMap {
          case Some(cidDetails) =>
            if (cidDetails.nino.toLowerCase == accountNino) {
              Future.successful(Redirect(appConfig.ivUpliftUrl(origin)))
            } else {
              Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin)))
            }
          case None =>
            warnLog("[KnownFactsService][checkCIDNinoComparison] Error Retrieving CID details. Empty data received from Citizens service")
            Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
        }
        .recover {
          case nfe: NotFoundException =>
            warnLog(s"[KnownFactsService][checkCIDNinoComparison] ${nfe.getMessage}")
            Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), origin))

          case e: Exception =>
            errorLog(s"[KnownFactsService][checkCIDNinoComparison] Error Retrieving CID details ${e.getMessage}")
            InternalServerError(errorHandler.internalServerErrorTemplate)
        }
    } else {
      warnLog(s"[KnownFactsService][checkCIDNinoComparison] Account authorization NINO and knownFactsNINO are not the same")
      Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad(origin)))

    }
  }

  def bypassOrCheckMandationStatus(
      submittedVrn: String)(implicit request: ServiceInfoRequest[AnyContent], ec: ExecutionContext, hc: HeaderCarrier): Future[Either[Result, Int]] =
    if (isEnabled(RealVatEtmpCheck)(appConfig)) {
      getMandationStatus(submittedVrn)
    } else {
      infoLog(s"[KnownFactsService][bypassOrCheckMandationStatus] RealVatEtmpCheck is disabled. No mandation check made")
      Future.successful(Right(OK))
    }

  private def getMandationStatus(
      vrn: String)(implicit request: ServiceInfoRequest[AnyContent], ec: ExecutionContext, hc: HeaderCarrier): Future[Either[Result, Int]] =
    vatSubscriptionConnector.getMandationStatus(vrn).map {
      case status if status == OK || status == NOT_FOUND =>
        infoLog(s"[KnownFactsService][getMandationStatus] mandation status: $status")
        Right(status)
      case PRECONDITION_FAILED =>
        infoLog(s"[KnownFactsService][getMandationStatus] mandation status: $PRECONDITION_FAILED")
        Left(Redirect(vatRoutes.WhatIsYourVATRegNumberController.onPageLoadVatUnavailable()))
      case status =>
        infoLog(s"[KnownFactsService][getMandationStatus] mandation status: $status")
        Left(InternalServerError(errorHandler.internalServerErrorTemplate))
    }

  def checkVrnMatchesPreviousAttempts(newSubmittedVrn: String, sessionId: String)(implicit
      request: ServiceInfoRequest[AnyContent],
      ec: ExecutionContext,
      hc: HeaderCarrier): Future[Either[Result, String]] = {
    val logPrefix = "[KnownFactsService][checkVrnMatchesPreviousAttempts]"
    if (isEnabled(VATKnownFactsCheck)) {
      infoLog(s"$logPrefix VATKnownFactsCheck switch is enabled")

      retrieveVRN(sessionId).flatMap {
        case None =>
          saveVRN(sessionId, newSubmittedVrn)
          infoLog(s"$logPrefix First time submitting VRN in journey")
          Future.successful(Right(newSubmittedVrn))
        case Some(previousAttemptVrn) if previousAttemptVrn.equals(newSubmittedVrn) =>
          infoLog(s"$logPrefix Submitted VRN matches previous VRN attempt in journey")
          Future.successful(Right(newSubmittedVrn))
        case Some(previousAttemptVrn) =>
          infoLog(s"$logPrefix Multiple VRNs attempted - saved VRN: $previousAttemptVrn, new VRN $newSubmittedVrn")

          val userType: String =
            request.request.affinityGroup.map(_.toString).getOrElse("Unknown")
          infoLog(s"$logPrefix Audit userType = $userType")

          auditService.auditCveMultipleVrnsAttempted(previousAttemptVrn, newSubmittedVrn, userType)

          val multipleVrnsAttemptedErrorRedirect: Call = vatRoutes.WhatIsYourVATRegNumberController.onPageLoadDifferentVatRegistrationNumbers()
          val signOutWithRedirectToErrorPage: String   = appConfig.addTaxesSignoutThenContinueTo(multipleVrnsAttemptedErrorRedirect.url)
          Future.successful(Left(Redirect(signOutWithRedirectToErrorPage)))
      }
    } else {
      infoLog(s"$logPrefix VATKnownFactsCheck switch is disabled")
      Future.successful(Right(newSubmittedVrn))
    }
  }

  private def saveVRN(sessionId: String, vrn: String): Future[CacheMap] =
    dataCacheConnector.save[String](sessionId, "vrn", vrn)
  private def retrieveVRN(sessionId: String): Future[Option[String]] =
    dataCacheConnector.getEntry[String](sessionId, "vrn")

}
