package utils

import base.SpecBase
import config.featureToggles.ConfigurableValue.IsBefore24thMarch
import config.featureToggles.FeatureSwitch.ECLSwitch

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeatureToggleSupportSpec extends SpecBase {

  override def afterAll(): Unit = {
    resetValue(ECLSwitch)
  }

  "FeatureToggleSupport" when {

    "interacting with feature switches" must {

      "be able to enable a feature switch" in {

        enable(ECLSwitch)
        sys.props.get(ECLSwitch.name) mustBe Some(true.toString)
      }

      "be able to disable a feature switch" in {

        disable(ECLSwitch)
        sys.props.get(ECLSwitch.name) mustBe Some(false.toString)
      }

      "be able to reset a value" in {

        val valueFromApplicationConf: Boolean = frontendAppConfig.config.getString(ECLSwitch.name).toBoolean

        setValue(ECLSwitch, (!valueFromApplicationConf).toString)
        valueFromApplicationConf == isEnabled(ECLSwitch) mustBe false

        resetValue(ECLSwitch)
        valueFromApplicationConf == isEnabled(ECLSwitch) mustBe true
      }

      "isEnabled" must {

        "be able to check the status of an enabled feature switch" in {

          enable(ECLSwitch)
          isEnabled(ECLSwitch) mustBe true
        }

        "be able to check the status of a disabled feature switch" in {

          disable(ECLSwitch)
          isEnabled(ECLSwitch) mustBe false
        }

        "load config from services config if nothing set in sysProps" in {

          sys.props -= ECLSwitch.name
          isEnabled(ECLSwitch) mustBe true
        }
      }

      "isDisabled" must {

        "be able to check the status of an enabled feature switch" in {

          enable(ECLSwitch)
          isDisabled(ECLSwitch) mustBe false
        }

        "be able to check the status of a disabled feature switch" in {

          disable(ECLSwitch)
          isDisabled(ECLSwitch) mustBe true
        }

        "load config from services config if nothing set in sysProps" in {

          sys.props -= ECLSwitch.name
          isDisabled(ECLSwitch) mustBe false
        }
      }
    }

    "interacting with configurable values" must {
      val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
      val today = LocalDateTime.now().format(dtf)

      "be able to retrieve a configurable value" in {
        getValue(IsBefore24thMarch) mustBe "202203240001"
      }

      "be able to set a configurable value" in {
        val allowList = today

        setValue(IsBefore24thMarch, allowList)
        getValue(IsBefore24thMarch) mustBe allowList
      }

      "be able to reset a configurable value" in {

        val valueFromApplicationConf: String = frontendAppConfig.config.getString(IsBefore24thMarch.name)
        val allowList = today

        setValue(IsBefore24thMarch, allowList)
        valueFromApplicationConf == getValue(IsBefore24thMarch) mustBe false

        resetValue(IsBefore24thMarch)
        valueFromApplicationConf == getValue(IsBefore24thMarch) mustBe true
      }
    }
  }
}

