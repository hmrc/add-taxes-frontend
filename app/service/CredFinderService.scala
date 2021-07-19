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
import controllers.Assets.{Ok, Redirect}
import controllers.sa.partnership.routes.DoYouWantToAddPartnerController
import models.requests.ServiceInfoRequest
import models.sa.SelectSACategory
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Enrolments.RegisterTrusts
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
    enrolments match {
      case HmrcEnrolmentType.SA() if mtdBool => SelectSACategory.options.filterNot(_.value == SelectSACategory.Sa.toString)
      case HmrcEnrolmentType.SA() => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Sa.toString || option.value == SelectSACategory.MtdIT.toString)
      case HmrcEnrolmentType.RegisterTrusts() =>
        SelectSACategory.options.filterNot(option => option.value == SelectSACategory.Trust.toString || option.value == SelectSACategory.MtdIT.toString)
      case _ => SelectSACategory.options.filterNot(option => option.value == SelectSACategory.MtdIT.toString)
    }
  }

  def redirectSACategory(form: Form[SelectSACategory],
                          action: Call,
                          origin: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent], messages: Messages): Future[Result] = {
    val enrolments = request.request.enrolments.enrolments
    for {mtdBoolCheck <- mtdItsaSubscribedCheck(enrolments)
         } yield {
      enrolments.map {
        enrolment =>
          enrolment.key match {
            case "IR-SA" if mtdBoolCheck => Future.successful(Redirect(controllers.sa.partnership.routes.DoYouWantToAddPartnerController.onPageLoad()))
            case "IR-SA" && "IR-SA-TRUST-ORG" => Future.successful(Redirect(controllers.sa.partnership.routes.DoYouWantToAddPartnerController.onPageLoad()))
            case _ => Future.successful(Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, mtdBoolCheck))(request.serviceInfoContent)))
          }
      }
    }
  }
}


  /*  def redirectSACategory(form: Form[SelectSACategory],
                         action: Call,
                         origin: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent], messages: Messages): Future[Result] = {
    val enrolments = request.request.enrolments.enrolments
    for { mtdBoolCheck <- mtdItsaSubscribedCheck(enrolments)
          } yield {
      request.request.enrolments match {
        case HmrcEnrolmentType.SA() if mtdBoolCheck => Redirect(controllers.sa.partnership.routes.DoYouWantToAddPartnerController.onPageLoad()) //change this
        case HmrcEnrolmentType.SA() && HmrcEnrolmentType.RegisterTrusts() => Redirect(controllers.sa.partnership.routes.DoYouWantToAddPartnerController.onPageLoad())
        case _ => Ok(selectSACategory(appConfig, form, action, origin, getRadioOptions(request.request.enrolments, mtdBoolCheck))(request.serviceInfoContent))
      }
    }
  }*/

  /*def redirectSACategory(form: Form[SelectSACategory],
                         action: Call,
                         origin: String)(implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent], messages: Messages): Future[Result] = {
    val enrolments = request.request.enrolments.enrolments
    for { mtdBoolCheck <- mtdItsaSubscribedCheck(enrolments)
          } yield {
    request.request.enrolments match {
      case HmrcEnrolmentType.SA() =>
        if (!enrolments.contains(RegisterTrusts) && mtdBoolCheck) {
          //Scenario 1
        } else {
          //Scenario 2
        }
      case _ => //Scenario 3
    }
  }
}


}*/

