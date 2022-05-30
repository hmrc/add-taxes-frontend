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

package testonly

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */
import config.FrontendAppConfig
import config.featureToggles.ConfigurableValue.{ConfigurableValue, configurableValues}
import config.featureToggles.FeatureSwitch.{FeatureSwitch, featureSwitches}
import config.featureToggles.{ConfigurableValue, FeatureSwitch, FeatureToggleSupport}
import javax.inject.Inject
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.feature_switch

import scala.collection.immutable.ListMap

class FeatureSwitchController @Inject()(val cc: MessagesControllerComponents,
                                        view: feature_switch
                                       )(implicit appConfig: FrontendAppConfig) extends FrontendController(cc) with I18nSupport with FeatureToggleSupport with Logging {

  def onPageLoad(): Action[AnyContent] = Action { implicit request =>

    val switches: Map[FeatureSwitch, Boolean] = ListMap(featureSwitches.map(switch => switch -> isEnabled(switch)): _*)
    val configValues: Map[ConfigurableValue, String] = ListMap(configurableValues.map(config => config -> getValue(config)): _*)

    val onSubmit = testonly.routes.FeatureSwitchController.onSubmit()
    val reset = testonly.routes.FeatureSwitchController.reset()

    Ok(view(switches, configValues, onSubmit, reset))
  }

  def onSubmit(): Action[AnyContent] = Action { implicit request =>

    val formSubmission: Map[String, Seq[String]] =
      request.body.asFormUrlEncoded.fold[Map[String, Seq[String]]](Map())(identity)

    val switchesWithData: Map[FeatureSwitch, Boolean] =
      formSubmission.map(keyValue => FeatureSwitch.get(keyValue._1) -> keyValue._2.head).collect {
        case (Some(key), value) => key -> value.toBoolean
      }

    val updatedConfigValues: Map[ConfigurableValue, String] =
      formSubmission.map(keyValue => ConfigurableValue.get(keyValue._1) -> keyValue._2.head).collect {
        case (Some(key), value) => key -> value
      }

    logger.debug(s"[FeatureSwitchController][submit] Feature switches: $switchesWithData")
    logger.debug(s"[FeatureSwitchController][submit] Configurable values > $updatedConfigValues")

    featureSwitches.foreach(switch => if (switchesWithData.exists(_._1 == switch)) enable(switch) else disable(switch))
    updatedConfigValues.foreach(config => setValue(config._1, config._2))
    Redirect(testonly.routes.FeatureSwitchController.onPageLoad())
  }

  def reset(): Action[AnyContent] = Action {

    featureSwitches.foreach(resetValue)
    configurableValues.foreach(resetValue)
    Redirect(testonly.routes.FeatureSwitchController.onPageLoad())
  }

}
