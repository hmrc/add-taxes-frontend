/*
 * Copyright 2023 HM Revenue & Customs
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

package config.featureToggles

object ConfigurableValue {

  val prefix = "feature-toggles"

  val configurableValues: Seq[ConfigurableValue] = Seq(
    IsBefore24thMarch
  )

  def apply(str: String): ConfigurableValue =
    configurableValues find (_.name == str) match {
      case Some(config) => config
      case None => throw new IllegalArgumentException("Invalid configurable value: " + str)
    }

  def get(string: String): Option[ConfigurableValue] = configurableValues find (_.name == string)

  sealed trait ConfigurableValue {
    val name: String
    val displayText: String
    val hint: Option[String] = None
  }

  case object IsBefore24thMarch extends ConfigurableValue {
    override val name: String = s"$prefix.isBefore24thMarch"
    override val displayText: String = "Is before 24th march?"
  }

}
