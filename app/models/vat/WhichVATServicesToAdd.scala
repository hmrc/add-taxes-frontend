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

package models.vat

import utils.{Enumerable, RadioOption, WithName}

sealed trait WhichVATServicesToAdd

object WhichVATServicesToAdd {

  case object VAT extends WithName("vat") with WhichVATServicesToAdd
  case object ECSales extends WithName("ecsales") with WhichVATServicesToAdd
  case object GIANT extends WithName("giant") with WhichVATServicesToAdd
  case object EURefunds extends WithName("eurefunds") with WhichVATServicesToAdd
  case object RCSL extends WithName("rcsl") with WhichVATServicesToAdd
  case object MOSS extends WithName("moss") with WhichVATServicesToAdd
  case object NOVA extends WithName("nova") with WhichVATServicesToAdd
  case object VATOSS extends WithName("vatoss") with WhichVATServicesToAdd

  val values: Seq[WhichVATServicesToAdd] = Seq(
    VAT,
    ECSales,
    GIANT,
    EURefunds,
    RCSL,
    MOSS,
    NOVA,
    VATOSS
  )

  val valuesWithOutMOSS: Seq[WhichVATServicesToAdd] = Seq(
    VAT,
    ECSales,
    GIANT,
    EURefunds,
    RCSL,
    NOVA,
    VATOSS
  )

  def options(ossFeatureSwitch: Boolean = false): Seq[RadioOption] = {
    val valuesSeq = {
      if (ossFeatureSwitch) {
        valuesWithOutMOSS.map { value =>
          RadioOption("whichVATServicesToAdd", value.toString)
        }
      } else {
        values.filterNot(x => x.equals(VATOSS)).map { value =>
          RadioOption("whichVATServicesToAdd", value.toString)
        }
      }
    }

    RadioOption("whichVATServicesToAdd", VAT.toString) +: valuesSeq.filter(x => x.value != VAT.toString).sortBy(_.value)
  }

  implicit val enumerable: Enumerable[WhichVATServicesToAdd] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
