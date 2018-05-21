/*
 * Copyright 2018 HM Revenue & Customs
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

package models.employer.cis.uk.contractor

import utils.{Enumerable, RadioOption, WithName}

sealed trait DoesBusinessManagePAYE

object DoesBusinessManagePAYE {

  case object Yes extends WithName("Yes") with DoesBusinessManagePAYE
  case object No extends WithName("No") with DoesBusinessManagePAYE

  val values: Set[DoesBusinessManagePAYE] = Set(
    Yes, No
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("doesBusinessManagePAYE", value.toString)
  }

  implicit val enumerable: Enumerable[DoesBusinessManagePAYE] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}