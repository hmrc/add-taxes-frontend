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

package models.deenrolment

import utils.{Enumerable, RadioOption, WithName}

sealed trait DoYouNeedToStopPBD

object DoYouNeedToStopPBD {

  case object Yes extends WithName("Yes") with DoYouNeedToStopPBD
  case object No extends WithName("No") with DoYouNeedToStopPBD

  val values: Set[DoYouNeedToStopPBD] = Set(
    Yes,
    No
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("doYouNeedToStopPBD", value.toString)
  }

  implicit val enumerable: Enumerable[DoYouNeedToStopPBD] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
