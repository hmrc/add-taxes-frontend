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

sealed abstract class Feature(val key: String) {
  require(key.nonEmpty)

  override val toString = s"${Feature.prefix}.$key"
}

object Feature {

  val prefix = "feature-toggles"

  def allTogglableFeatures: Set[Feature] = Set(
  )

  def fromQuery(key: String): Option[Feature] =
    allTogglableFeatures.collectFirst {
      case feature if feature.key.toLowerCase == key.toLowerCase => feature
    }

}
