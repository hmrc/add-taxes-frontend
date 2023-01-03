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

package models.vat.moss

import utils.{Enumerable, RadioOption, WithName}

sealed trait WhereIsYourBusinessBased

object WhereIsYourBusinessBased {

  case object UK extends WithName("UK") with WhereIsYourBusinessBased
  case object EU extends WithName("EU") with WhereIsYourBusinessBased
  case object NonEu extends WithName("NonEu") with WhereIsYourBusinessBased
  case object Iom extends WithName("Iom") with WhereIsYourBusinessBased

  val values: Set[WhereIsYourBusinessBased] = Set(
    UK,
    EU,
    NonEu,
    Iom
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("whereIsYourBusinessBased", value.toString)
  }

  implicit val enumerable: Enumerable[WhereIsYourBusinessBased] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
