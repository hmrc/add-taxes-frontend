/*
 * Copyright 2021 HM Revenue & Customs
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

package models.vat.moss.noneu

import utils.{Enumerable, RadioOption, WithName}

sealed trait HaveYouRegisteredForVATMOSS

object HaveYouRegisteredForVATMOSS {

  case object Yes extends WithName("Yes") with HaveYouRegisteredForVATMOSS
  case object No extends WithName("No") with HaveYouRegisteredForVATMOSS

  val values: Set[HaveYouRegisteredForVATMOSS] = Set(
    Yes,
    No
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("haveYouRegisteredForVATMOSS", value.toString)
  }

  implicit val enumerable: Enumerable[HaveYouRegisteredForVATMOSS] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
