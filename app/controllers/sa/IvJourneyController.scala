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

package controllers.sa

import config.FrontendAppConfig
import controllers.actions.{AuthAction, ServiceInfoAction}
import javax.inject.Inject
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.IvService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import controllers.sa.{routes => saRoutes}

import scala.concurrent.{ExecutionContext, Future}

class IvJourneyController @Inject()(ivService: IvService,
                                    mcc: MessagesControllerComponents,
                                    authenticate: AuthAction,
                                    serviceInfoData: ServiceInfoAction,
                                   appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with Logging {

  implicit val ec: ExecutionContext = mcc.executionContext

  def ivRouter(origin: String, journeyId: Option[String] = None): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
        if(appConfig.ivUpliftFeatureSwitch) {
          journeyId match {
            case Some(id) => ivService.ivCheckAndEnrolUplift(origin, id)
            case _ =>
              logger.error("[IvJourneyController][ivRouter] Error there is no JounreyId provided on call")
              Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), origin)))
          }
        } else {
          ivService.ivCheckAndEnrol(origin)
        }
  }

}