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

package models.employer.cis.uk.subcontractor

import utils.{Enumerable, RadioOption, WithName}

sealed trait WhatTypeOfSubcontractor

object WhatTypeOfSubcontractor {

  case object SoleTrader extends WithName("soleTrader") with WhatTypeOfSubcontractor
  case object Partnership extends WithName("partnership") with WhatTypeOfSubcontractor
  case object LimitedCompany extends WithName("limitedCompany") with WhatTypeOfSubcontractor

  val values: Set[WhatTypeOfSubcontractor] = Set(
    SoleTrader,
    Partnership,
    LimitedCompany
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("whatTypeOfSubcontractor", value.toString)
  }

  implicit val enumerable: Enumerable[WhatTypeOfSubcontractor] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
