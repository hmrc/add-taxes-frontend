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

import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class FeatureConfigSpec extends WordSpec with MustMatchers with GuiceOneAppPerSuite with FeatureToggleSupport with BeforeAndAfterEach {

  val testToggleValue = true

  override implicit lazy val app: Application = GuiceApplicationBuilder()
    .configure(s"feature-toggles.${NewVatJourney.key}" -> testToggleValue.toString)
    .build()

  val appConf: ServicesConfig = app.injector.instanceOf[ServicesConfig]
  val config: FeatureConfig = app.injector.instanceOf[FeatureConfig]

  override def beforeEach(): Unit = {
    super.beforeEach()
    Feature.allTogglableFeatures.foreach(removeOverride)
  }

  "FeatureConfig.isEnabled" when {
    "there is a runtime override" should {
      "return true if the override for the feature is true" in {
        appConf.getBoolean(NewVatJourney.toString) mustBe testToggleValue

        enable(NewVatJourney)
        config.isEnabled(NewVatJourney) mustBe true
      }
      "return false if the override for the feature is false" in {
        appConf.getBoolean(NewVatJourney.toString) mustBe testToggleValue

        disable(NewVatJourney)
        config.isEnabled(NewVatJourney) mustBe false
      }
    }
    "there is not a runtime override" should {
      "return true if the config for the feature is true" in {
        appConf.getBoolean(NewVatJourney.toString) mustBe testToggleValue

        config.isEnabled(NewVatJourney) mustBe testToggleValue
      }
    }
  }

}
