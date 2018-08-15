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

package forms.sa

import forms.behaviours.FormBehaviours
import generators.ModelGenerators
import models.sa.SAUTR
import models.{Field, Required}
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import play.api.data.Form

class SAUTRFormProviderSpec extends FormBehaviours with GeneratorDrivenPropertyChecks with ModelGenerators {

  override val validData: Map[String, String] = Map("value" -> "0987654321")
  override val form: Form[_] = new SAUTRFormProvider()()

  private val utrLength = 10
  private val min = 0
  private val max = 9

  val validGen: Gen[SAUTR] = saUtrGen(listOfN(utrLength, choose(min, max)))

  val invalidLengthGen: Gen[SAUTR] =
    saUtrGen(listOf(choose(min, max)), (p: List[_]) => p.length != utrLength && p.nonEmpty)

  val invalidCharGen: Gen[SAUTR] = saUtrGen(listOfN(utrLength, asciiChar), (p: List[Any]) => !p.contains(' '))

  "SAUTRFormProver" must {

    behave like formWithMandatoryTextFields(
      Field("value", Required -> "enterSAUTR.error.required")
    )

    "fail for invalid lengths" in {
      forAll(invalidLengthGen) { value =>
        form
          .bind(Map("value" -> value.value))
          .fold(
            formWithErrors => formWithErrors.error("value").map(_.message) shouldBe Some("enterSAUTR.error.length"),
            _ => fail("This form should not succeed")
          )
      }
    }

    "fail for invalid characters" in {
      forAll(invalidCharGen) { value =>
        form
          .bind(Map("value" -> value.value))
          .fold(
            formWithErrors =>
              formWithErrors.errors("value").map(_.message) shouldBe List("enterSAUTR.error.characters"),
            _ => fail("This form should not succeed")
          )
      }
    }

    "pass for valid entries of digits and spaces" in {
      forAll(validGen) { value =>
        form
          .bind(Map("value" -> value.value))
          .fold(
            formWithErrors => fail(s"This form should be valid, Error = ${formWithErrors.errors.map(_.message)}"),
            form => form shouldBe value
          )
      }
    }
  }

}
