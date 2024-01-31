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

package models.employer.paye

import utils.{Enumerable, RadioOption, WithName}

sealed trait DoesBusinessHave1To9Directors

object DoesBusinessHave1To9Directors {

  case object Yes extends WithName("Yes") with DoesBusinessHave1To9Directors
  case object No extends WithName("No") with DoesBusinessHave1To9Directors

  val values: Set[DoesBusinessHave1To9Directors] = Set(
    Yes,
    No
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("doesBusinessHave1To9Directors", value.toString)
  }

  implicit val enumerable: Enumerable[DoesBusinessHave1To9Directors] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
