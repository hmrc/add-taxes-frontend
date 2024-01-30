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

package controllers.sa

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.IvUpliftSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import connectors.DataCacheConnector
import controllers.actions.{AuthAction, ServiceInfoAction}
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.sa.SAUTR
import utils.LoggingUtil
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import service.SaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class TryIvController @Inject()(authenticate: AuthAction,
                                serviceInfoData: ServiceInfoAction,
                                mcc: MessagesControllerComponents,
                                saService: SaService,
                                dataCacheConnector: DataCacheConnector,
                                implicit val appConfig: FrontendAppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggingUtil {

  implicit val ec: ExecutionContext = mcc.executionContext

  def onPageLoad(origin: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
        val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
        val urlString: Future[String] = utr.flatMap {
          case Some(utr) =>
            if(isEnabled(IvUpliftSwitch)) {
              Future.successful(appConfig.ivUpliftUrl(origin))
            } else {
              saService.getIvRedirectLink(utr.value, origin)
            }
          case _ =>
            warnLog("[TryIvController][onPageLoad] Failed to get UTR from DataCache")
            Future.successful(routes.TryPinInPostController.onPageLoad(Some("MatchingError"), origin).url)
        }

      for (
        redirectUrl <- urlString
      ) yield Redirect(Call("GET", redirectUrl))
  }

}