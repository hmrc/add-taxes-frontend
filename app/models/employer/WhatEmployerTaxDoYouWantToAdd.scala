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

package models.employer

import uk.gov.hmrc.auth.core.Enrolments
import utils.{Enumerable, HmrcEnrolmentType, RadioOption, WithName}
import play.api.mvc.Request

sealed trait WhatEmployerTaxDoYouWantToAdd

object WhatEmployerTaxDoYouWantToAdd {

  case object EPAYE extends WithName("Epaye") with WhatEmployerTaxDoYouWantToAdd
  case object CIS extends WithName("Cis") with WhatEmployerTaxDoYouWantToAdd
  case object PS extends WithName("Pension") with WhatEmployerTaxDoYouWantToAdd
  case object ERS extends WithName("Ers") with WhatEmployerTaxDoYouWantToAdd
  case object EIA extends WithName("Intermediaries") with WhatEmployerTaxDoYouWantToAdd

  case object ERSWithEnrolments extends WithName("ErsHasEpaye") with WhatEmployerTaxDoYouWantToAdd
  case object EIAWithEnrolments extends WithName("IntermediariesHasEpaye") with WhatEmployerTaxDoYouWantToAdd

  val values: Seq[WhatEmployerTaxDoYouWantToAdd] = Seq(
    EPAYE,
    CIS,
    PS,
    ERS,
    EIA
  )

  val valuesWithEpayeEnrolments: Seq[WhatEmployerTaxDoYouWantToAdd] = Seq(
    CIS,
    PS,
    ERSWithEnrolments,
    EIAWithEnrolments
  )

  val options: Seq[RadioOption] = values.map { value =>
    RadioOption("whatEmployerTaxDoYouWantToAdd", value.toString)
  }

  val optionsWithEpaye: Seq[RadioOption] = valuesWithEpayeEnrolments.map { value =>
    RadioOption("whatEmployerTaxDoYouWantToAdd", value.toString)
  }

  implicit val enumerable: Enumerable[WhatEmployerTaxDoYouWantToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
