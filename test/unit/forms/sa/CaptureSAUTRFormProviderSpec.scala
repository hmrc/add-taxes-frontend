package forms.sa

import forms.behaviours.FormBehaviours
import models._
import models.sa._
import play.api.data.Form

class CaptureSAUTRFormProviderSpec extends FormBehaviours {

  override val validData: Map[String, String] = Map("sautrValue" -> "0987654321")
  override val form: Form[CaptureSAUTRModel] = new CaptureSAUTRFormProvider()()

  val validUtr: String = "1 2 3 4 5 6 7 8 0 0"
  val validUtrExtended: String = validUtr + "123"
  val invalidLengthUtr = "12345678009"
  val invalidCharUtr: String = s"123456@£%^"

  "CaptureSAUTRFormProvider" must {

    behave like formWithMandatoryTextFields(
      Field("value", Required -> "captureSAUTR.error.required")
    )

    "fail for invalid lengths" in {
      form
        .bind(Map("value" -> "Yes", "sautrValue" -> invalidLengthUtr))
        .fold(
          formWithErrors => formWithErrors.error("sautrValue").map(_.message) mustBe Some("captureSAUTR.utr.error.length"),
          _ => fail("This form should not succeed")
        )
    }
  }

  "fail for invalid characters" in {
    form
      .bind(Map("value" -> "Yes", "sautrValue" -> invalidCharUtr))
      .fold(
        formWithErrors =>
          formWithErrors.errors("sautrValue").map(_.message) mustBe List("captureSAUTR.utr.error.characters"),
        _ => fail("This form should not succeed")
      )
  }

  "pass for valid entries of digits and spaces" in {
    form
      .bind(Map("value" -> "Yes", "sautrValue" -> validUtr))
      .fold(
        formWithErrors => fail(s"This form should be valid, Error = ${formWithErrors.errors.map(_.message)}"),
        sautr => sautr.sautrValue.get mustBe SAUTR(validUtr).value
      )
  }

  "pass for valid entries with extra 3 digits at the start and spaces" in {
    form
      .bind(Map("value" -> "Yes", "sautrValue" -> validUtrExtended))
      .fold(
        formWithErrors => fail(s"This form should be valid, Error = ${formWithErrors.errors.map(_.message)}"),
        sautr => sautr.sautrValue.get mustBe SAUTR(validUtrExtended).value
      )
  }

}
