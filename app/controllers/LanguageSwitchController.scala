/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers

import config.FrontendAppConfig
import javax.inject.Inject
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.language.LanguageUtils
import LanguageSwitchController._

class LanguageSwitchController @Inject()(configuration: ServicesConfig,
                                         appConfig: FrontendAppConfig,
                                         languageUtils: LanguageUtils,
                                         mcc: MessagesControllerComponents)
  extends FrontendController(mcc) with I18nSupport {

  private def fallbackURL: String = routes.IndexController.onPageLoad().url

  private def languageMap: Map[String, Lang] = appConfig.languageMap

  def switchToLanguage(language: String): Action[AnyContent] = controllerComponents.actionBuilder { implicit request =>
    val enabled = isWelshEnabled
    val lang = if (enabled) {
      languageMap.getOrElse(language, languageUtils.getCurrentLang)
    } else {
      english
    }
    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)
    Redirect(redirectURL).withLang(Lang.apply(lang.code)).flashing(FlashWithSwitchIndicator)
  }

  private def isWelshEnabled: Boolean =
    configuration.getBoolean("microservice.services.features.welsh-translation")
}

object LanguageSwitchController {
  private val SwitchIndicatorKey: String      = "switching-language"
  private val FlashWithSwitchIndicator: Flash = Flash(Map(SwitchIndicatorKey -> "true"))
  val english: Lang                           = Lang("en")
  val welsh: Lang                             = Lang("cy")
}