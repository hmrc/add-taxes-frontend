/*
 * Copyright 2024 HM Revenue & Customs
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

package models.other.gambling

import utils.{Enumerable, RadioOption, WithName}

sealed trait SelectGamblingOrGamingDuty

object SelectGamblingOrGamingDuty {

  case object MGD extends WithName("MGD") with SelectGamblingOrGamingDuty
  case object GBD extends WithName("GBD") with SelectGamblingOrGamingDuty
  case object PBD extends WithName("PBD") with SelectGamblingOrGamingDuty
  case object RGD extends WithName("RGD") with SelectGamblingOrGamingDuty

  val values: Set[SelectGamblingOrGamingDuty] = Set(
    MGD,
    GBD,
    PBD,
    RGD
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("selectGamblingOrGamingDuty", value.toString)
  }

  implicit val enumerable: Enumerable[SelectGamblingOrGamingDuty] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
