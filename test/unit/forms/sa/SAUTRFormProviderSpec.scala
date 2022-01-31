/*
 * Copyright 2020 HM Revenue & Customs
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

package forms.sa

import forms.behaviours.FormBehaviours
import models.sa.SAUTR
import models.{Field, Required}
import play.api.data.Form

class SAUTRFormProviderSpec extends FormBehaviours {

  override val validData: Map[String, String] = Map("value" -> "0987654321")
  override val form: Form[SAUTR] = new SAUTRFormProvider()()

  val validUtr: String = "1 2 3 4 5 6 7 8 0 0"
  val validUtrExtended: String = validUtr + "123"
  val invalidLengthUtr = "12345678009"
  val invalidCharUtr: String = s"123456@£%^"

  "SAUTRFormProver" must {

    behave like formWithMandatoryTextFields(
      Field("value", Required -> "enterSAUTR.error.required")
    )

    "fail for invalid lengths" in {
        form
          .bind(Map("value" -> invalidLengthUtr))
          .fold(
            formWithErrors => formWithErrors.error("value").map(_.message) mustBe Some("enterSAUTR.error.length"),
            _ => fail("This form should not succeed")
          )
      }
    }

    "fail for invalid characters" in {
        form
          .bind(Map("value" -> invalidCharUtr))
          .fold(
            formWithErrors =>
              formWithErrors.errors("value").map(_.message) mustBe List("enterSAUTR.error.characters"),
            _ => fail("This form should not succeed")
          )
    }

    "pass for valid entries of digits and spaces" in {
        form
          .bind(Map("value" -> validUtr))
          .fold(
            formWithErrors => fail(s"This form should be valid, Error = ${formWithErrors.errors.map(_.message)}"),
            sautr => sautr.value mustBe SAUTR(validUtr).value
          )
    }

    "pass for valid entries with extra 3 digits at the start and spaces" in {
        form
          .bind(Map("value" -> validUtrExtended))
          .fold(
            formWithErrors => fail(s"This form should be valid, Error = ${formWithErrors.errors.map(_.message)}"),
            sautr => sautr.value mustBe SAUTR(validUtrExtended).value
          )
      }
}
