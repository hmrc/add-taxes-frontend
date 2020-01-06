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

package models.employer.cis.uk.subcontractor

import utils.{Enumerable, RadioOption, WithName}

sealed trait WasTurnoverMoreAfterVAT

object WasTurnoverMoreAfterVAT {

  case object Yes extends WithName("Yes") with WasTurnoverMoreAfterVAT
  case object No extends WithName("No") with WasTurnoverMoreAfterVAT

  val values: Set[WasTurnoverMoreAfterVAT] = Set(
    Yes,
    No
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("wasTurnoverMoreAfterVAT", value.toString)
  }

  implicit val enumerable: Enumerable[WasTurnoverMoreAfterVAT] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
