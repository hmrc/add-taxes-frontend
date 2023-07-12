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

import base.SpecBase
import config.featureToggles.FeatureSwitch
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class FeatureObjectSpec extends SpecBase {

  "Feature.allTogglableFeatures" should {
    "have an associated config value under feature-toggles" in {
      val config = app.injector.instanceOf[ServicesConfig]
      FeatureSwitch.featureSwitches foreach { feature =>
        withClue(s"feature: ${feature.name}\n") {
          Option(config.getBoolean(feature.name.toString)).isDefined mustBe true
        }
      }
    }
  }

}
