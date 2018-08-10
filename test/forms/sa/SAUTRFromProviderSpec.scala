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

  val validGen: Gen[SAUTR] = for {
    digits <- listOfN(10, choose(0, 9))
    spaces <- listOf(const(" "))
    random = shuffle(digits ++ spaces)
  } yield {
    SAUTR(random.mkString)
  }

  val invalidLengthGen: Gen[SAUTR] = for {
    digits <- listOf(choose(0, 9))
    if digits.length != 10
    spaces <- listOf(const(" "))
    random = shuffle(digits ++ spaces)
  } yield {
    SAUTR(random.mkString)
  }

  val invalidCharGen: Gen[SAUTR] = for {
    chars <- listOfN(10, asciiChar)
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

  }

}
