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

import config.featureToggles.FeatureSwitch.{ECLSwitch, FeatureSwitch, Pillar2Switch, VapingDutySwitch}
import models.OtherTaxes
import models.OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration
import uk.gov.hmrc.auth.core
import uk.gov.hmrc.auth.core.Enrolment

sealed trait Enrolments {
  val toAuthEnrolment: Enrolment = Enrolment(this.toString)
  val identifier: String         = this.toString
}

sealed trait OtherTaxEnrolment extends Enrolments {
  val toOtherTaxRadioOption: RadioOption
  val featureSwitch: Option[FeatureSwitch]
}

sealed trait GamblingAndGaming extends OtherTaxEnrolment {
  override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.GamblingAndGaming.toRadioOption
  override val featureSwitch: Option[FeatureSwitch] = None
}

sealed trait OilAndFuel extends OtherTaxEnrolment {
  override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.OilAndFuel.toRadioOption
  override val featureSwitch: Option[FeatureSwitch] = None
}

sealed trait PODS extends OtherTaxEnrolment {
  override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.PODS.toRadioOption
  override val featureSwitch: Option[FeatureSwitch] = None
}

object Enrolments {

  case object AddCis extends WithName("HMRC-CIS-ORG") with Enrolments

  case object ATaR extends WithName("HMRC-ATAR-ORG") with Enrolments

  case object ATED extends WithName("HMRC-ATED-ORG") with Enrolments

  // Alcohol & Tobacco Warehousing Declarations
  case object ATWD extends WithName("HMCE-ATWD-ORG") with Enrolments

  // Alcohol Wholesaler Registration Scheme
  case object AWRS extends WithName("HMRC-AWRS-ORG") with Enrolments

  // CommonTransitConvention is the new NewComputerisedTransitSystem
  case object CommonTransitConvention extends WithName("HMRC-CTC-ORG") with Enrolments

  case object CustomsDeclarationServices extends WithName("HMRC-CUS-ORG") with Enrolments

  case object CT extends WithName("IR-CT") with Enrolments

  case object DefermentApprovalNumber extends WithName("HMCE-DDES") with Enrolments

  case object EconomicOperatorsRegistration extends WithName("HMRC-ICS-ORG") with Enrolments

  case object ECSales extends WithName("HMCE-ECSL-ORG") with Enrolments

  case object ECW extends WithName("HMRC-ECW-IND") with Enrolments

  case object ElectronicBindingTariffInformation extends WithName("HMCE-EBTI-ORG") with Enrolments

  case object EPAYE extends WithName("IR-PAYE") with Enrolments

  case object EURefunds extends WithName("HMRC-EU-REF-ORG") with Enrolments

  case object ExciseMovementControlSystem extends WithName("HMRC-EMCS-ORG") with Enrolments

  case object NewComputerisedTransitSystem extends WithName("HMCE-NCTS-ORG") with Enrolments

  case object NewExportSystem extends WithName("HMCE-NES") with Enrolments

  case object PP extends WithName("HMRC-PP-ORG") with Enrolments

  case object PSA extends WithName("HMRC-PSA-ORG") with Enrolments

  case object SA extends WithName("IR-SA") with Enrolments

  case object SDLT extends WithName("IR-SDLT-ORG") with Enrolments

  case object PPT extends WithName("HMRC-PPT-ORG") with Enrolments

  case object RegisterTrusts extends WithName("IR-SA-TRUST-ORG") with Enrolments

  case object SAPartnership extends WithName("IR-SA-PART-ORG") with Enrolments

  case object VAT extends WithName("HMCE-VATDEC-ORG") with Enrolments

  case object MTDVAT extends WithName("HMRC-MTD-VAT") with Enrolments

  case object VATGIANT extends WithName("HMRC-VAT-GIANT") with Enrolments

  case object VATMOSS extends WithName("HMRC-MOSS-U-ORG") with Enrolments

  case object VATMOSSNonUnion extends WithName("HMRC-MOSSNU-ORG") with Enrolments

  case object VATVAR extends WithName("HMCE-VATVAR-ORG") with Enrolments

  case object AEOI extends WithName("HMRC-FATCA-ORG") with OtherTaxEnrolment {
    override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.AutomaticExchangeOfInformation.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = None
  }

  case object Charities extends WithName("HMRC-CHAR-ORG") with OtherTaxEnrolment {
    override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.Charities.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = None
  }

  case object CTF extends WithName("IR-CTF") with OtherTaxEnrolment {
    override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.ChildTrustFund.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = None
  }

  case object ECL extends WithName("HMRC-ECL-ORG") with OtherTaxEnrolment {
    override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.ECL.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = Some(ECLSwitch)
  }

  case object OtherBusinessTaxDutyScheme extends WithName("HMRC-OBTDS-ORG") with OtherTaxEnrolment {
    val FulfilmentHouseDueDiligenceSchemeIdentifier   = "EtmpRegistrationNumber"
    override val identifier: String                   = FulfilmentHouseDueDiligenceSchemeIdentifier
    override val toOtherTaxRadioOption: RadioOption   = FulfilmentHouseDueDiligenceSchemeIntegration.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = None
  }

  case object PLRID extends WithName("HMRC-PILLAR2-ORG") with OtherTaxEnrolment {
    override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.PLRID.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = Some(Pillar2Switch)
  }

  case object VPD extends WithName("HMRC-VPD-ORG") with OtherTaxEnrolment {
    override val toOtherTaxRadioOption: RadioOption   = OtherTaxes.VapingDuty.toRadioOption
    override val featureSwitch: Option[FeatureSwitch] = Some(VapingDutySwitch)
  }

  case object GeneralBetting extends WithName("HMRC-GTS-GBD") with GamblingAndGaming

  case object MachineGamingDuty extends WithName("HMRC-MGD-ORG") with GamblingAndGaming

  case object PoolBetting extends WithName("HMRC-GTS-PBD") with GamblingAndGaming

  case object RemoteGaming extends WithName("HMRC-GTS-RGD") with GamblingAndGaming

  val allGamblingAndGaming: Set[GamblingAndGaming] = Set(GeneralBetting, MachineGamingDuty, PoolBetting, RemoteGaming)

  case object RebatedOils extends WithName("HMCE-RO") with OilAndFuel

  case object TiedOils extends WithName("HMCE-TO") with OilAndFuel

  val allOilAndFuel: Set[OilAndFuel] = Set(RebatedOils, TiedOils)

  case object PODSORG extends WithName("HMRC-PODS-ORG") with PODS

  case object PODSPP extends WithName("HMRC-PODSPP-ORG") with PODS

  val allPODS: Set[PODS] = Set(PODSORG, PODSPP)

  val values: Set[Enrolments] = Set(
    ECL,
    PLRID,
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
    ATaR,
    VPD
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

  case object VPD extends WithName("HMRC-VPD-ORG") with HmrcEnrolmentType

  val values: Set[HmrcEnrolmentType] = Set(SA, CORP_TAX, EPAYE, VAT, RegisterTrusts, SDLT, PSA, PP, OSS, VPD)
}
