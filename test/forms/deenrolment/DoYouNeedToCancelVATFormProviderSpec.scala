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

package forms.deenrolment

import forms.behaviours.FormBehaviours
import models._
import models.deenrolment._

class DoYouNeedToCancelVATFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> DoYouNeedToCancelVAT.options.head.value
  )

  val form = new DoYouNeedToCancelVATFormProvider()()

  "DoYouNeedToCancelVAT form" must {

    behave like questionForm[DoYouNeedToCancelVAT](DoYouNeedToCancelVAT.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "doYouNeedToCancelVAT.error.required", Invalid -> "error.invalid"),
      DoYouNeedToCancelVAT.options.toSeq.map(_.value): _*)
  }
}
