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

package models

import utils.{Enumerable, RadioOption, WithName}

sealed trait OtherTaxes {
  val toRadioOption = RadioOption("otherTaxes", this.toString)
}

object OtherTaxes {

  case object AlcoholAndTobacco extends WithName("alcoholAndTobaccoWholesalingAndWarehousing") with OtherTaxes
  case object AutomaticExchangeOfInformation extends WithName("automaticExchangeOfInformation") with OtherTaxes
  case object Charities extends WithName("charities") with OtherTaxes
  case object GamblingAndGaming extends WithName("gamblingAndGaming") with OtherTaxes
  case object ChildTrustFund extends WithName("childTrustFund") with OtherTaxes
  case object HousingAndLand extends WithName("housingAndLand") with OtherTaxes
  case object ImportsExports extends WithName("importsExports") with OtherTaxes
  case object OilAndFuel extends WithName("oilAndFuel") with OtherTaxes
  case object PODS extends WithName("pods") with OtherTaxes
  case object PPT extends WithName("ppt") with OtherTaxes
  case object FulfilmentHouseDueDiligenceSchemeIntegration
      extends WithName("fulfilmentHouseDueDiligenceSchemeIntegration")
      with OtherTaxes

  val values: Seq[OtherTaxes] = Seq(
    AlcoholAndTobacco,
    AutomaticExchangeOfInformation,
    Charities,
    GamblingAndGaming,
    ChildTrustFund,
    HousingAndLand,
    ImportsExports,
    OilAndFuel,
    FulfilmentHouseDueDiligenceSchemeIntegration,
    PODS,
    PPT
  )

  val options: Seq[RadioOption] = values.map {
    _.toRadioOption
  }

  implicit val enumerable: Enumerable[OtherTaxes] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
