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

package config.featureToggles

object FeatureSwitch {

  val prefix = "feature-toggles"

  val featureSwitches: Seq[FeatureSwitch] = Seq(
    AccessMTD,
    IvUpliftSwitch,
    AtarSwitch,
    RealVatEtmpCheck,
    NewCTCEnrolmentForNCTSJourney,
    Pillar2Switch,
    CDSSwitch,
    AWRSJourneyRedirect,
    VATKnownFactsCheck,
    VANContentChanges
  )

  def apply(str: String): FeatureSwitch =
    featureSwitches find (_.name == str) match {
      case Some(switch) => switch
      case None         => throw new IllegalArgumentException("Invalid feature switch: " + str)
    }

  def get(string: String): Option[FeatureSwitch] = featureSwitches find (_.name == string)

  def fromQuery(key: String): Option[FeatureSwitch] =
    featureSwitches.collectFirst {
      case feature if feature.name.toLowerCase == key.toLowerCase => feature
    }

  trait FeatureSwitch {
    val name: String
    val displayText: String
    val hint: Option[String] = None
  }

  case object AccessMTD extends FeatureSwitch {
    override val name                = s"$prefix.accessMTD"
    override val displayText: String = "Access MTD Enabled"
  }

  case object IvUpliftSwitch extends FeatureSwitch {
    override val name                = s"$prefix.ivUpliftSwitch"
    override val displayText: String = "Iv Uplift Enabled"
  }

  case object ECLSwitch extends FeatureSwitch {
    override val name                = s"$prefix.eclSwitch"
    override val displayText: String = "PPT Enabled"
  }

  case object AtarSwitch extends FeatureSwitch {
    override val name                = s"$prefix.atarSwitch"
    override val displayText: String = "Atar Enabled"
  }

  // TODO this config name doesn't match the functionality.
  //  We should either change the name in config or flip the config Boolean in the different envs
  //  (would need an overlap for prod safety)
  case object RealVatEtmpCheck extends FeatureSwitch {
    override val name                = s"$prefix.bypassVATETMPCheck"
    override val displayText: String = "Make the real VAT ETMP check rather than passing by default"
  }

  case object NewCTCEnrolmentForNCTSJourney extends FeatureSwitch {
    override val name: String        = s"$prefix.newCTCEnrolmentForNCTSJourney"
    override val displayText: String = "Enable new HMRC - CTC - ORG enrolment journey for NCTS"
  }

  case object Pillar2Switch extends FeatureSwitch {
    override val name                = s"$prefix.pillar2Switch"
    override val displayText: String = "pillar2 tax feature Enabled"
  }

  case object CDSSwitch extends FeatureSwitch {
    override val name: String        = s"$prefix.cdsSwitch"
    override val displayText: String = "Add and remove CDS tax enabled"
  }

  case object AWRSJourneyRedirect extends FeatureSwitch {
    override val name: String        = s"$prefix.awrsJourneyRedirect"
    override val displayText: String = "Enable redirect for AWRS registration check"
  }

  case object VATKnownFactsCheck extends FeatureSwitch {
    override val name: String        = s"$prefix.vatKnownFactsCheck"
    override val displayText: String = "VAT Known Facts changes"
  }

  case object VANContentChanges extends FeatureSwitch {
    override val name: String        = s"$prefix.vanContentChanges"
    override val displayText: String = "Feature switch for VAN Content changes"
  }

}
