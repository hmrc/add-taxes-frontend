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
import models.sa.SAUTR
import models.{Field, Required}
import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import play.api.data.Form
import scala.util.Random.shuffle

class SAUTRFromProviderSpec extends FormBehaviours with GeneratorDrivenPropertyChecks {

  override val validData: Map[String, String] = Map("value" -> "0987654321")
  override val form: Form[_] = new SAUTRFromProvider()()

  private val utrLength = 10
  private val min = 0
  private val max = 9

  val validGen: Gen[SAUTR] =
    for {
      digits <- listOfN(utrLength, choose(min, max))
      spaces <- listOf(const(" "))
      random = shuffle(digits ++ spaces)
    } yield {
      SAUTR(random.mkString)
    }

  val invalidLengthGen: Gen[SAUTR] =
    for {
      digits <- listOf(choose(min, max))
      if digits.length != utrLength && digits.nonEmpty
      spaces <- listOf(const(" "))
      random = shuffle(digits ++ spaces)
    } yield {
      SAUTR(random.mkString)
    }

  val invalidCharGen: Gen[SAUTR] =
    for {
      chars <- listOfN(utrLength, asciiChar)
      if !chars.contains(' ')
      spaces <- listOf(const(" "))
      random = shuffle(chars ++ spaces)
    } yield {
      SAUTR(random.mkString)
    }

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
            form => form == value
          )
      }
    }
  }

}
