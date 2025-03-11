/*
 * Copyright 2024 HM Revenue & Customs
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
import uk.gov.hmrc.auth.core.Enrolment

sealed trait Enrolments {
  val toAuthEnrolment: Enrolment = Enrolment(this.toString)
}

object Enrolments {

  case object RebatedOils extends WithName("HMCE-RO") with Enrolments

  case object TiedOils extends WithName("HMCE-TO") with Enrolments

  case object ATWD extends WithName("HMCE-ATWD-ORG") with Enrolments

  case object AWRS extends WithName("HMRC-AWRS-ORG") with Enrolments

  case object AEOI extends WithName("HMRC-FATCA-ORG") with Enrolments

  case object ATED extends WithName("HMRC-ATED-ORG") with Enrolments

  case object Charities extends WithName("HMRC-CHAR-ORG") with Enrolments

  case object ECL extends WithName("HMRC-ECL-ORG") with Enrolments

  case object MachineGamingDuty extends WithName("HMRC-MGD-ORG") with Enrolments

  case object GeneralBetting extends WithName("HMRC-GTS-GBD") with Enrolments

  case object MachineGamesDuty extends WithName("HMRC-MGD-ORG") with Enrolments

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

  case object NewComputerisedTransitSystem extends WithName("HMCE-NCTS-ORG") with Enrolments

  case object CustomsDeclarationServices extends WithName("HMRC-CUS-ORG") with Enrolments

  // CommonTransitConvention is the new NewComputerisedTransitSystem
  case object CommonTransitConvention extends WithName("HMRC-CTC-ORG") with Enrolments

  case object SAPartnership extends WithName("IR-SA-PART-ORG") with Enrolments

  case object RegisterTrusts extends WithName("IR-SA-TRUST-ORG") with Enrolments

  case object VAT extends WithName("HMCE-VATDEC-ORG") with Enrolments

  case object MTDVAT extends WithName("HMRC-MTD-VAT") with Enrolments

  case object VATGIANT extends WithName("HMRC-VAT-GIANT") with Enrolments

  case object VATMOSS extends WithName("HMRC-MOSS-U-ORG") with Enrolments

  case object AddCis extends WithName("HMRC-CIS-ORG") with Enrolments

  case object VATMOSSNonUnion extends WithName("HMRC-MOSSNU-ORG") with Enrolments

  case object ECSales extends WithName("HMCE-ECSL-ORG") with Enrolments

  case object EURefunds extends WithName("HMRC-EU-REF-ORG") with Enrolments

  case object EPAYE extends WithName("IR-PAYE") with Enrolments

  case object PP extends WithName("HMRC-PP-ORG") with Enrolments

  case object VATVAR extends WithName("HMCE-VATVAR-ORG") with Enrolments

  case object PSA extends WithName("HMRC-PSA-ORG") with Enrolments

  case object SA extends WithName("IR-SA") with Enrolments

  case object CT extends WithName("IR-CT") with Enrolments

  case object CTF extends WithName("IR-CTF") with Enrolments

  case object ECW extends WithName("HMRC-ECW-IND") with Enrolments

  case object SDLT extends WithName("IR-SDLT-ORG") with Enrolments

  case object PODSORG extends WithName("HMRC-PODS-ORG") with Enrolments

  case object PODSPP extends WithName("HMRC-PODSPP-ORG") with Enrolments

  case object PPT extends WithName("HMRC-PPT-ORG") with Enrolments

  case object ATaR extends WithName("HMRC-ATAR-ORG") with Enrolments

  case object PLRID extends WithName("HMRC-PILLAR2-ORG") with Enrolments


  val values: Set[Enrolments] = Set(
    RebatedOils,
    TiedOils,
    ATWD,
    AWRS,
    AWRS,
    AEOI,
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
    MTDVAT,
    AddCis,
    VATMOSSNonUnion,
    ECSales,
    EURefunds,
    EPAYE,
    ExciseMovementControlSystem,
    PP,
    VATVAR,
    ATED,
    PSA,
    SA,
    VATGIANT,
    CT,
    CTF,
    VATMOSS,
    ECW,
    SDLT,
    PODSORG,
    PODSPP,
    PPT,
    ATaR
  )
}

object && {
  def unapply[A](a: A): Option[(A, A)] = Some((a, a))
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

  case object MTDVAT extends WithName("HMRC-MTD-VAT") with HmrcEnrolmentType

  case object MTDIT extends WithName("HMRC-MTD-IT") with HmrcEnrolmentType

  case object RegisterTrusts extends WithName("IR-SA-TRUST-ORG") with HmrcEnrolmentType

  case object Partnerships extends WithName("IR-SA-PART-ORG") with HmrcEnrolmentType

  case object SDLT extends WithName("IR-SDLT-ORG") with HmrcEnrolmentType

  case object PSA extends WithName("HMRC-PSA-ORG") with HmrcEnrolmentType

  case object PP extends WithName("HMRC-PP-ORG") with HmrcEnrolmentType

  case object OSS extends WithName("HMRC-OSS-ORG") with HmrcEnrolmentType

  val values: Set[HmrcEnrolmentType] = Set(SA, CORP_TAX, EPAYE, VAT, RegisterTrusts, SDLT, PSA, PP, OSS)
}
