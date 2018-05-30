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

package models.vat

import utils.{Enumerable, RadioOption, WithName}

sealed trait WhichVATServicesToAdd

object WhichVATServicesToAdd {

  case object VAT extends WithName("vat") with WhichVATServicesToAdd
  case object ECSales extends WithName("ecsales") with WhichVATServicesToAdd
  case object EURefunds extends WithName("eurefunds") with WhichVATServicesToAdd
  case object RCSL extends WithName("rcsl") with WhichVATServicesToAdd
  case object MOSS extends WithName("moss") with WhichVATServicesToAdd
  case object NOVA extends WithName("nova") with WhichVATServicesToAdd

  val values: Seq[WhichVATServicesToAdd] = Seq(
    VAT,
    ECSales,
    EURefunds,
    RCSL,
    MOSS,
    NOVA
  )

  val options: Seq[RadioOption] = values.map { value =>
    RadioOption("whichVATServicesToAdd", value.toString)
  }

  implicit val enumerable: Enumerable[WhichVATServicesToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
