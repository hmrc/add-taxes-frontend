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

package utils

sealed trait Enrolments

object Enrolments {

  case object RebatedOils extends WithName("HMCE-RO") with Enrolments

  case object TiedOils extends WithName("HMCE-TO") with Enrolments

  case object AlcoholAndTobaccoWarehousingDeclarations extends WithName("HMCE-ATWD-ORG") with Enrolments

  case object AlcoholWholesalerRegistrationScheme extends WithName("HMRC-AWRS-ORG") with Enrolments

  case object AutomaticExchangeOfInformation extends WithName("HMRC-FATCA-ORG") with Enrolments

  case object Charities extends WithName("HMRC-CHAR-ORG") with Enrolments

  case object MachineGamingDuty extends WithName("HMRC-MGD-ORG") with Enrolments

  case object GeneralBetting extends WithName("HMRC-GTS-GBD") with Enrolments

  case object PoolBetting extends WithName("HMRC-GTS-PBD") with Enrolments

  case object RemoteGaming extends WithName("HMRC-GTS-RGD") with Enrolments

  case object OtherBusinessTaxDutyScheme extends WithName("HMRC-OBTDS-ORG") with Enrolments {
    val FulfilmentHouseDueDiligenceSchemeIdentifier = "EtmpRegistrationNumber"
  }

  case object ExciseMovementControlSystem extends WithName("HMRC-EMCS-ORG") with Enrolments

  val values: Set[Enrolments] = Set(
    RebatedOils, TiedOils, AlcoholAndTobaccoWarehousingDeclarations, AlcoholWholesalerRegistrationScheme,
    AlcoholWholesalerRegistrationScheme, AutomaticExchangeOfInformation, Charities, MachineGamingDuty, GeneralBetting,
    PoolBetting, RemoteGaming, OtherBusinessTaxDutyScheme
  )
}
