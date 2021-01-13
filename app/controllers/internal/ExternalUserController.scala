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

package controllers.internal

import config.FrontendAppConfig
import controllers.actions.{AuthAction, ServiceInfoAction}
import javax.inject.Inject
import models.sa.SaEnrolmentDetails
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.IvService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class ExternalUserController @Inject()(appConfig: FrontendAppConfig,
                                       mcc: MessagesControllerComponents,
                                       ivService: IvService)(implicit ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport with Logging {


  def initiateJourney(): Action[AnyContent] = Action.async(parse.anyContent) {
    implicit request =>
       request.body.asJson.get.validate[SaEnrolmentDetails] match {
         case s: JsSuccess[SaEnrolmentDetails] =>
           val details = s.get
           validateDetails(details) match {
             case (true, true) => ivService.journeyRouter(details).map {
               url => Ok(Json.obj("redirectUrl" -> url))
             }
             case (false, true) => Future.successful(BadRequest(s"Invalid origin: ${details.origin}"))
             case (true, false) => Future.successful(BadRequest(s"Invalid utr: ${details.utr.get}"))
             case (false, false) => Future.successful(BadRequest(s"Invalid origin: ${details.origin}, Invalid utr: ${details.utr.get}"))
           }
         case e: JsError => Future.successful(BadRequest(s"Failed to parse Json with error: " + JsError.toJson(e).toString()))
       }
  }

  def validateDetails(details: SaEnrolmentDetails): (Boolean, Boolean) = {
    val originBool = appConfig.validOrigins.contains(details.origin)
    val utrBool = details.utr match {
      case Some(utr) => utr.matches("^\\d{10}$")
      case _ => true
    }

    (originBool, utrBool)
  }
}

