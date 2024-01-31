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

package models.vat

import utils.{Enumerable, RadioOption, WithName}

sealed trait VatRegistrationException

object VatRegistrationException {

  case object Yes extends WithName("Yes") with VatRegistrationException
  case object No extends WithName("No") with VatRegistrationException

  val values: Set[VatRegistrationException] = Set(
    Yes,
    No
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("radio", value.toString)
  }

  implicit val enumerable: Enumerable[VatRegistrationException] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
