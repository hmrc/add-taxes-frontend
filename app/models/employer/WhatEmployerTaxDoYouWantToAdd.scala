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

package models.employer

import utils.{Enumerable, RadioOption, WithName}

sealed trait WhatEmployerTaxDoYouWantToAdd

object WhatEmployerTaxDoYouWantToAdd {

  case object EPAYE extends WithName("epaye") with WhatEmployerTaxDoYouWantToAdd
  case object CIS extends WithName("cis") with WhatEmployerTaxDoYouWantToAdd
  case object PS extends WithName("pension") with WhatEmployerTaxDoYouWantToAdd
  case object ERS extends WithName("ers") with WhatEmployerTaxDoYouWantToAdd
  case object EIA extends WithName("intermediaries") with WhatEmployerTaxDoYouWantToAdd

  val values: Seq[WhatEmployerTaxDoYouWantToAdd] = Seq(
    EPAYE,
    CIS,
    PS,
    ERS,
    EIA
  )

  val options: Seq[RadioOption] = values.map { value =>
    RadioOption("whatEmployerTaxDoYouWantToAdd", value.toString)
  }

  implicit val enumerable: Enumerable[WhatEmployerTaxDoYouWantToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
