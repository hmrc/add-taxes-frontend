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

package forms.employer.paye

import forms.behaviours.FormBehaviours
import models.employer.paye.PAYEReference
import models.{Field, Required}
import play.api.data.Form

class PAYEReferenceFormProviderSpec extends FormBehaviours {

  override val validData: Map[String, String] = Map("officeNumber" -> "123", "payeReference" -> "AB123")
  override val form: Form[PAYEReference] = new PAYEReferenceFormProvider()()

  "SAUTRFormProver" must {

    behave like formWithMandatoryTextFields(
      Field("officeNumber", Required -> "enterYourPAYEReference.officeNumber.error.required"),
      Field("payeReference", Required -> "enterYourPAYEReference.employerPayeReference.error.required")
    )

    "fail for invalid characters" in {
      form
        .bind(Map("officeNumber" -> "A%G", "payeReference" -> "£][sd%"))
        .fold(
          formWithErrors =>
            formWithErrors.errors.flatMap(_.messages) mustBe List("enterYourPAYEReference.officeNumber.error.format", "enterYourPAYEReference.employerPayeReference.error.format"),
          _ => fail("This form should not succeed")
        )
    }

    "pass for valid officeNumber and payeReference" in {
      form
        .bind(Map("officeNumber" -> "123", "payeReference" -> "AB123"))
        .fold(
          formWithErrors => fail(s"This form should be valid, Error = ${formWithErrors.errors.map(_.message)}"),
          empRef => empRef mustBe PAYEReference("123", "AB123")
        )

    }

  }

}
