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
import connectors.{CitizensDetailsConnector, DataCacheConnector, GetBusinessDetailsConnector}
import controllers.sa.trust.routes.HaveYouRegisteredTrustController
import controllers.Assets.{Ok, Redirect}
import controllers.sa.partnership.routes.DoYouWantToAddPartnerController
import models.requests.ServiceInfoRequest
import models.sa.SelectSACategory
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CredFinderService @Inject()(citizensDetailsConnector: CitizensDetailsConnector,
                                  getBusinessDetailsConnector: GetBusinessDetailsConnector,
                                  appConfig: FrontendAppConfig,
                                  selectSACategory: selectSACategory,
                                  dataCacheConnector: DataCacheConnector) {

  def mtdItsaSubscribedCheck(enrolments: Set[Enrolment])(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Boolean] = {
    enrolments.map {
      enrolment =>
        enrolment.key match {
          case "IR-SA" => val utr = enrolment.getIdentifier("utr")
            utr match {
              case Some(id) => mtdITSASignupBool(enrolment.key, id)
              case _ => Future.successful(false)
            }
          case _ => Future.successful(false)
        }
    }.head
  }

  def mtdITSASignupBool(key: String, utr: EnrolmentIdentifier)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[Boolean] = {
    citizensDetailsConnector.getDesignatoryDetails(key, utr.value).flatMap {
      case Some(details) => getBusinessDetailsConnector.getBusinessDetails("nino", details.nino).map {
        case Some(_) => {
          dataCacheConnector.save[Boolean](request.request.credId, "mtdItSignupBoolean", true)
          true
        }
        case _ => {
          false
        }
      }
      case _ => Future.successful(false)
    }
  }

  def getRadioOptions(enrolments: Enrolments, mtdBool: Boolean): Set[RadioOption] = {

    enrolmentTuple(enrolments) match {
      case (true, _, _, true) => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Sa.toString || option.value == SelectSACategory.MtdIT.toString)
      case (true, _, _, false) if !mtdBool => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Sa.toString || option.value == SelectSACategory.MtdIT.toString)
      case(_, true, true, _) if mtdBool => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Partnership.toString || option.value == SelectSACategory.Trust.toString)
      case (_, true, true, _) => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Partnership.toString || option.value == SelectSACategory.Trust.toString || option.value == SelectSACategory.MtdIT.toString)
      case (_, true, _, _) if mtdBool => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Trust.toString || option.value == SelectSACategory.Sa.toString)
      case (_, true, _, _) => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Trust.toString || option.value == SelectSACategory.MtdIT.toString)
      case (_, _, true, _) if mtdBool => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Partnership.toString || option.value == SelectSACategory.Sa.toString)
      case (_, _, true, _) => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Partnership.toString || option.value == SelectSACategory.MtdIT.toString)
      case _ if mtdBool => SelectSACategory.options.filterNot(_.value == SelectSACategory.Sa.toString)
      case _ => SelectSACategory.options.filterNot(_.value == SelectSACategory.MtdIT.toString)
    }
  }

  def redirectSACategory(form: Form[SelectSACategory],
                    action: Call,
                    origin: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent], messages: Messages): Future[Result] = {

    val enrolments = request.request.enrolments

    enrolmentTuple(enrolments) match {
      case (true, true, false, true) => Future.successful(Redirect(DoYouWantToAddPartnerController.onPageLoad()))
      case (true, false, true, true) => Future.successful(Redirect(HaveYouRegisteredTrustController.onPageLoad()))
      case (true, _, _, true) => Future.successful(Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, false))(request.serviceInfoContent)))
      case (_, _, _, true) => Future.successful(Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, false))(request.serviceInfoContent)))
      case (_, _, _, _) =>
        for {mtdBoolCheck <- mtdItsaSubscribedCheck(enrolments.enrolments)
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

}

