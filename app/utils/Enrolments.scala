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

import uk.gov.hmrc.auth.core

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

  case object EconomicOperatorsRegistration extends WithName("HMRC-ICS-ORG") with Enrolments

  case object OtherBusinessTaxDutyScheme extends WithName("HMRC-OBTDS-ORG") with Enrolments {
    val FulfilmentHouseDueDiligenceSchemeIdentifier = "EtmpRegistrationNumber"
  }

  case object ExciseMovementControlSystem extends WithName("HMRC-EMCS-ORG") with Enrolments

  case object DefermentApprovalNumber extends WithName("HMCE-DDES") with Enrolments

  case object NewExportSystem extends WithName("HMCE-NES") with Enrolments

  case object ElectronicBindingTariffInformation extends WithName("HMCE-EBTI-ORG") with Enrolments

  case object NewComputerisedTransitSystem extends WithName("HMRC-NCTS-ORG") with Enrolments

  case object SAPartnership extends WithName("IR-SA-PART-ORG") with Enrolments

  case object RegisterTrusts extends WithName("IR-SA-TRUST-ORG") with Enrolments

  case object VAT extends WithName("HMCE-VATDEC-ORG") with Enrolments

  case object VATMOSS extends WithName("HMRC-MOSS-U-ORG") with Enrolments

  case object Charity extends WithName("HMRC-CHAR-ORG") with Enrolments

  case object AddCis extends WithName("HMRC-CIS-ORG") with Enrolments

  case object VATMOSSNonUnion extends WithName("HMRC-MOSSNU-ORG") with Enrolments

  case object ECSales extends WithName("HMCE-ECSL-ORG") with Enrolments

  case object EURefunds extends WithName("HMRC-EU-REF-ORG") with Enrolments

  case object RCSL extends WithName("HMCE-VATRSL-ORG") with Enrolments

  case object EPAYE extends WithName("IR-PAYE") with Enrolments

  val values: Set[Enrolments] = Set(
    RebatedOils,
    TiedOils,
    AlcoholAndTobaccoWarehousingDeclarations,
    AlcoholWholesalerRegistrationScheme,
    AlcoholWholesalerRegistrationScheme,
    AutomaticExchangeOfInformation,
    Charities,
    MachineGamingDuty,
    GeneralBetting,
    PoolBetting,
    RemoteGaming,
    OtherBusinessTaxDutyScheme,
    EconomicOperatorsRegistration,
    DefermentApprovalNumber,
    NewExportSystem,
    ElectronicBindingTariffInformation,
    NewComputerisedTransitSystem,
    NewExportSystem,
    SAPartnership,
    RegisterTrusts,
    VAT,
    Charity,
    AddCis,
    VATMOSSNonUnion,
    ECSales,
    EURefunds,
    RCSL,
    EPAYE
  )

  def hasEnrolments(enrolments: core.Enrolments, enrolmentTypes: HmrcEnrolmentType*) = enrolmentTypes.exists { t =>
    enrolments.getEnrolment(t.toString).isDefined
  }

}

sealed trait HmrcEnrolmentType {
  def unapply(enrolments: core.Enrolments): Boolean =
    enrolments.getEnrolment(this.toString).isDefined
}

object HmrcEnrolmentType {

  case object SA extends WithName("IR-SA") with HmrcEnrolmentType

  case object CORP_TAX extends WithName("IR-CT") with HmrcEnrolmentType

  case object EPAYE extends WithName("IR-PAYE") with HmrcEnrolmentType

  case object VAT extends WithName("HMCE-VATDEC-ORG") with HmrcEnrolmentType

  case object RegisterTrusts extends WithName("IR-SA-TRUST-ORG") with HmrcEnrolmentType

  val values: Set[HmrcEnrolmentType] = Set(SA, CORP_TAX, EPAYE, VAT, RegisterTrusts)
}
