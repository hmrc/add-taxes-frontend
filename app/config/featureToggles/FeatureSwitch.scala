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

package config.featureToggles

object FeatureSwitch {

  val prefix = "feature-toggles"

  val featureSwitches: Seq[FeatureSwitch] = Seq(
    AccessMTD,
    IvUpliftSwitch,
    PptSwitch,
    AtarSwitch,
    ARSContentSwitch,
    BypassVATETMPCheck,
    NewCTCEnrolmentForNCTSJourney
  )

  def apply(str: String): FeatureSwitch =
    featureSwitches find (_.name == str) match {
      case Some(switch) => switch
      case None => throw new IllegalArgumentException("Invalid feature switch: " + str)
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
    override val name = s"$prefix.accessMTD"
    override val displayText: String = "Access MTD Enabled"
  }

  case object IvUpliftSwitch extends FeatureSwitch {
    override val name = s"$prefix.ivUpliftSwitch"
    override val displayText: String = "Iv Uplift Enabled"
  }

  case object PptSwitch extends FeatureSwitch {
    override val name = s"$prefix.pptSwitch"
    override val displayText: String = "PPT Enabled"
  }

  case object IossSwitch extends FeatureSwitch {
    override val name = s"$prefix.iossSwitch"
    override val displayText: String = "IOSS Enabled"
  }

  case object ECLSwitch extends FeatureSwitch {
    override val name = s"$prefix.eclSwitch"
    override val displayText: String = "PPT Enabled"
  }

  case object AtarSwitch extends FeatureSwitch {
    override val name = s"$prefix.atarSwitch"
    override val displayText: String = "Atar Enabled"
  }


  case object ARSContentSwitch extends FeatureSwitch {
    override val name = s"$prefix.arsContentSwitch"
    override val displayText: String = "ARS content feature enabled"
  }


  case object BypassVATETMPCheck  extends FeatureSwitch {
    override val name = s"$prefix.bypassVATETMPCheck"
    override val displayText: String = "Bypass VAT ETMP Check"
  }

  case object NewCTCEnrolmentForNCTSJourney extends FeatureSwitch {
    override val name: String = s"$prefix.newCTCEnrolmentForNCTSJourney"
    override val displayText: String = "Enable new HMRC - CTC - ORG enrolment journey for NCTS"
  }
}

