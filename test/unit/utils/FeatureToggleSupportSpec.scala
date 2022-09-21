package utils

import config.FrontendAppConfig
import config.featureToggles.ConfigurableValue.IsBefore24thMarch
import config.featureToggles.FeatureSwitch.VatOssSwitch
import config.featureToggles.FeatureToggleSupport
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FeatureToggleSupportSpec extends PlaySpec with FeatureToggleSupport with BeforeAndAfterAll with GuiceOneAppPerSuite {
  implicit val frontendAppConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  override def afterAll(): Unit = {
    resetValue(VatOssSwitch)
  }

  "FeatureToggleSupport" when {

    "interacting with feature switches" must {

      "be able to enable a feature switch" in {

        enable(VatOssSwitch)
        sys.props.get(VatOssSwitch.name) mustBe Some(true.toString)
      }

      "be able to disable a feature switch" in {

        disable(VatOssSwitch)
        sys.props.get(VatOssSwitch.name) mustBe Some(false.toString)
      }

      "be able to reset a value" in {

        val valueFromApplicationConf: Boolean = frontendAppConfig.config.getString(VatOssSwitch.name).toBoolean

        setValue(VatOssSwitch, (!valueFromApplicationConf).toString)
        valueFromApplicationConf == isEnabled(VatOssSwitch) mustBe false

        resetValue(VatOssSwitch)
        valueFromApplicationConf == isEnabled(VatOssSwitch) mustBe true
      }

      "isEnabled" must {

        "be able to check the status of an enabled feature switch" in {

          enable(VatOssSwitch)
          isEnabled(VatOssSwitch) mustBe true
        }

        "be able to check the status of a disabled feature switch" in {

          disable(VatOssSwitch)
          isEnabled(VatOssSwitch) mustBe false
        }

        "load config from services config if nothing set in sysProps" in {

          sys.props -= VatOssSwitch.name
          isEnabled(VatOssSwitch) mustBe false
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

