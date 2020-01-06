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

package playconfig.featuretoggle

import javax.inject.Inject
import play.api.Environment
import uk.gov.hmrc.play.config.ServicesConfig

import scala.util.Try

class FeatureConfig @Inject()(override val runModeConfiguration: play.api.Configuration, environment: Environment)
    extends ServicesConfig {

  override protected def mode: play.api.Mode.Mode = environment.mode

  def isEnabled(feature: Feature): Boolean =
    sys.props
      .get(feature.toString)
      .flatMap(prop => Try(prop.toBoolean).toOption)
      .orElse(runModeConfiguration.getBoolean(feature.toString))
      .getOrElse(false)

}
