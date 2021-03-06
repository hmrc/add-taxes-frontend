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

package testonly

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import playconfig.featuretoggle.{Feature, FeatureConfig, FeatureToggleSupport}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class GetFeaturesController @Inject()(config: FeatureConfig, mcc: MessagesControllerComponents)
  extends FrontendController(mcc) with FeatureToggleSupport {

  def getAllFeatures: Action[AnyContent] = Action {
    val features = Feature.allTogglableFeatures
    Ok(Json.toJson(features.map(feature => Json.obj(feature.key -> config.isEnabled(feature)))))
  }

}
