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

package models.deenrolment

import utils.{Enumerable, RadioOption, WithName}

sealed trait StopCorporationTax

object StopCorporationTax {

  case object Stop extends WithName("stop") with StopCorporationTax
  case object Dormant extends WithName("dormant") with StopCorporationTax
  case object Close extends WithName("close") with StopCorporationTax

  val values: Set[StopCorporationTax] = Set(
    Stop,
    Dormant,
    Close
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("stopCorporationTax", value.toString)
  }

  implicit val enumerable: Enumerable[StopCorporationTax] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
