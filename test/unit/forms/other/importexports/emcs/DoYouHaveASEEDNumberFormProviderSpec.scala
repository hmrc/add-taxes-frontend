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

package forms.other.importexports.emcs

import forms.behaviours.FormBehaviours
import models._
import models.other.importexports.emcs.DoYouHaveASEEDNumber

class DoYouHaveASEEDNumberFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> DoYouHaveASEEDNumber.options.head.value
  )

  val form = new DoYouHaveASEEDNumberFormProvider()()

  "DoYouHaveASEEDNumber form" must {

    behave like questionForm[DoYouHaveASEEDNumber](DoYouHaveASEEDNumber.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "doYouHaveASEEDNumber.error.required", Invalid -> "error.invalid"),
      DoYouHaveASEEDNumber.options.toSeq.map(_.value): _*)
  }
}
