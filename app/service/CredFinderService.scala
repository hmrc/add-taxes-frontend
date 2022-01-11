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
import connectors.{CitizensDetailsConnector, DataCacheConnector, SaConnector}
import identifiers.EnterSAUTRId
import controllers.sa.partnership.routes.DoYouWantToAddPartnerController
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{SAUTR, SelectSACategory}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

import scala.concurrent.{ExecutionContext, Future}

class CredFinderService @Inject()(citizensDetailsConnector: CitizensDetailsConnector,
                                  saConnector: SaConnector,
                                  appConfig: FrontendAppConfig,
                                  selectSACategory: selectSACategory,
                                  dataCacheConnector: DataCacheConnector) {
  def mtdItsaSubscribedCheck(enrolmentIdentifier: Option[EnrolmentIdentifier])
                            (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Boolean] = {
    enrolmentIdentifier match {
      case Some(utr) =>
        citizensDetailsConnector.getDesignatoryDetails("IR-SA", utr.value).flatMap {
          case Some(details) => saConnector.getBusinessDetails(details.nino, "nino").map {
            case Some(_) =>
              dataCacheConnector.save[Boolean](request.request.credId, "mtdItSignupBoolean", true)
              true
            case _ => false
          }
          case _ => Future.successful(false)
        }
      case _ => Future.successful(false)
    }
  }


  def getRadioOptions(enrolments: Enrolments, mtdBool: Boolean): Set[RadioOption] = {

    val categories: Set[String] = transformNonITSAEnrolmentsToSelectSACategories(enrolments)

    val activeRadioButtons: Set[RadioOption] = SelectSACategory.options.filterNot(option => categories.contains(option.value))

    val saEnrolment: Boolean = enrolments.getEnrolment(HmrcEnrolmentType.SA.toString).isDefined
    val mtdEnrolment: Boolean = enrolments.getEnrolment(HmrcEnrolmentType.MTDIT.toString).isDefined

    (mtdEnrolment, saEnrolment, mtdBool) match {
      case (false, true, true) => activeRadioButtons
      case(false, false, true) => activeRadioButtons.filterNot(option => option.value == SelectSACategory.Sa.toString)
      case _ => activeRadioButtons.filterNot(option => option.value == SelectSACategory.MtdIT.toString)
    }

  }

  def getEnrolmentIdentifier(enrolments: Enrolments): Option[EnrolmentIdentifier] = {
    enrolments.enrolments.collectFirst{case enrolment: Enrolment if enrolment.key == "IR-SA" => enrolment} match {
      case Some(enrolment) => enrolment.getIdentifier("utr")
      case _ => None
    }
  }

  def redirectSACategory(form: Form[SelectSACategory], action: Call, origin: String)
                        (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent], messages: Messages): Future[Result] = {

    val enrolments = request.request.enrolments

    enrolmentTuple(enrolments) match {
      case (true, true, false, true) => Future.successful(Redirect(DoYouWantToAddPartnerController.onPageLoad()))
      case (true, true, true, true) => Future.successful(Redirect(DoYouWantToAddPartnerController.onPageLoad()))
      case (true, _, _, true) => Future.successful(
        Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, false))(request.serviceInfoContent))
      )
      case (true, true, true, false) =>
        for {mtdBoolCheck <- mtdItsaSubscribedCheck(getEnrolmentIdentifier(enrolments))
             } yield {
          if(mtdBoolCheck){
            Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, mtdBoolCheck))(request.serviceInfoContent))
          } else {
            Redirect(DoYouWantToAddPartnerController.onPageLoad())
          }
        }
      case (_, _, _, true) => Future.successful(
        Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, mtdBool = false))(request.serviceInfoContent))
      )
      case (true, _, _, _) =>
        for {mtdBoolCheck <- mtdItsaSubscribedCheck(getEnrolmentIdentifier(enrolments))
             } yield {
            Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, mtdBoolCheck))(request.serviceInfoContent))
        }
      case (_, _, _, _) =>
        for {
          utr <- dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
          mtdBoolCheck <- mtdItsaSubscribedCheck(Some(EnrolmentIdentifier("IR-SA", utr.getOrElse(SAUTR("")).value)))
             } yield {
            Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, mtdBoolCheck))(request.serviceInfoContent))
        }
    }
    }

  private def enrolmentTuple(enrolments: Enrolments):(Boolean, Boolean, Boolean, Boolean) = {
    val saEnrolment: Boolean = enrolments.getEnrolment(HmrcEnrolmentType.SA.toString).isDefined
    val saRegisterTrustsEnrolment: Boolean = enrolments.getEnrolment(HmrcEnrolmentType.RegisterTrusts.toString).isDefined
    val saPartnershipsEnrolment: Boolean = enrolments.getEnrolment(HmrcEnrolmentType.Partnerships.toString).isDefined
    val mtdEnrolment: Boolean = enrolments.getEnrolment(HmrcEnrolmentType.MTDIT.toString).isDefined

    (saEnrolment, saRegisterTrustsEnrolment, saPartnershipsEnrolment, mtdEnrolment)
  }

  private def transformNonITSAEnrolmentsToSelectSACategories(enrolments: Enrolments): Set[String] = {

    enrolments.enrolments.flatMap {
      case e: Enrolment if e.key == HmrcEnrolmentType.SA.toString => Some(SelectSACategory.Sa.toString)
      case e: Enrolment if e.key == HmrcEnrolmentType.RegisterTrusts.toString => Some(SelectSACategory.Trust.toString)
      case _ => None
    }

  }

}

