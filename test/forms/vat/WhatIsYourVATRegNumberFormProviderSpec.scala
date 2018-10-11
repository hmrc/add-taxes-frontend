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

package forms.vat

import forms.behaviours.FormBehaviours
import models.{Field, Required}

class WhatIsYourVATRegNumberFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> "968501689"
  )
  val validVrnLength = 9
  val form = new WhatIsYourVATRegNumberFormProvider()()

  "DoYouHaveVATRegNumber form" must {

    behave like questionForm[String]("968501689")

    behave like formWithMandatoryTextFields(
      Field("value", Required -> "whatIsYourVATRegNumber.error.required")
    )

    "fail to bind when the VRN contains a non-digit character" in {
      val data = Map("value" -> "a")
      val expectedError = error("value", "whatIsYourVATRegNumber.error.required", """\d*""")
      checkForError(form, data, expectedError)
    }

    "fail to bind when the VRN is too long" in {
      val data = Map("value" -> "01234567891234")
      val expectedError = error("value", "whatIsYourVATRegNumber.error.tooLong", validVrnLength)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the VRN is too short" in {
      val data = Map("value" -> "1234")
      val expectedError = error("value", "whatIsYourVATRegNumber.error.tooShort", validVrnLength)
      checkForError(form, data, expectedError)
    }

    "fail to bind when the VRN passes all other constraints but is still an invalid VRN format" in {
      val data = Map("value" -> "999999999")
      val expectedError = error("value", "whatIsYourVATRegNumber.error.required")
      checkForError(form, data, expectedError)
    }
  }
}
