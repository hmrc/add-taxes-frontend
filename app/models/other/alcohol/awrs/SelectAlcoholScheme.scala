/*
 * Copyright 2026 HM Revenue & Customs
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

package models.other.alcohol.awrs

import utils.{Enrolments, Enumerable, RadioOption, WithName}

sealed trait SelectAlcoholScheme {
  def toRadioOption: RadioOption = RadioOption("selectAlcoholScheme", this.toString)
}

object SelectAlcoholScheme {

  case object ATWD extends WithName("atwd") with SelectAlcoholScheme
  case object AWRS extends WithName("awrs") with SelectAlcoholScheme
  case object AD   extends WithName("ad") with SelectAlcoholScheme
  case object VPD extends WithName("vpd") with SelectAlcoholScheme {
    val enrolmentKey: String = Enrolments.VPD.identifier
  }

  val values: Set[SelectAlcoholScheme] = Set(
    ATWD,
    AWRS,
    AD,
    VPD
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("selectAlcoholScheme", value.toString)
  }

  implicit val enumerable: Enumerable[SelectAlcoholScheme] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
