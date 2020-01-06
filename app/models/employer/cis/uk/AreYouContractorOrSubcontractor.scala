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

package models.employer.cis.uk

import utils.{Enumerable, RadioOption, WithName}

sealed trait AreYouContractorOrSubcontractor

object AreYouContractorOrSubcontractor {

  case object Contractor extends WithName("Contractor") with AreYouContractorOrSubcontractor
  case object Subcontractor extends WithName("Subcontractor") with AreYouContractorOrSubcontractor

  val values: Set[AreYouContractorOrSubcontractor] = Set(
    Contractor,
    Subcontractor
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("areYouContractorOrSubcontractor", value.toString)
  }

  implicit val enumerable: Enumerable[AreYouContractorOrSubcontractor] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
