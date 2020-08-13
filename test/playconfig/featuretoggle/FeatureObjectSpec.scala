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

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class FeatureObjectSpec extends WordSpec with MustMatchers with GuiceOneAppPerSuite {

  "Feature.allTogglableFeatures" should {
    "contain all members of Feature" in {
      import scala.reflect.runtime.universe
      val mirror = universe.runtimeMirror(getClass.getClassLoader)

      import scala.reflect.runtime.universe._

      def sealedDescendants[Root: TypeTag]: Set[Symbol] = {
        val symbol = typeOf[Root].typeSymbol
        val internal = symbol.asInstanceOf[scala.reflect.internal.Symbols#Symbol]
        if (internal.isSealed) { internal.sealedDescendants.map(_.asInstanceOf[Symbol]) - symbol } else { Set.empty }
      }

      val descendants = sealedDescendants[Feature]

      val features: Set[Feature] = descendants.collect {
        case desc if desc.isModuleClass =>
          val module = mirror.staticModule(desc.asClass.fullName)
          mirror.reflectModule(module).instance.asInstanceOf[Feature]
      }
      Feature.allTogglableFeatures mustBe features
    }

    "have an associated config value under feature-toggles" in {
      val config = app.injector.instanceOf[ServicesConfig]
      Feature.allTogglableFeatures foreach { feature =>
        withClue(s"feature: $feature\n") {
          Option(config.getBoolean(feature.toString)).isDefined mustBe true
        }
      }
    }
  }

}
