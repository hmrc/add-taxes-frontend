/*
 * Copyright 2019 HM Revenue & Customs
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

package forms.vat.moss.noneu

import forms.behaviours.FormBehaviours
import models._
import models.vat.moss.noneu._

class HaveYouRegisteredForVATMOSSFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> HaveYouRegisteredForVATMOSS.options.head.value
  )

  val form = new HaveYouRegisteredForVATMOSSFormProvider()()

  "HaveYouRegisteredForVATMOSS form" must {

    behave like questionForm[HaveYouRegisteredForVATMOSS](HaveYouRegisteredForVATMOSS.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "haveYouRegisteredForVATMOSS.error.required", Invalid -> "error.invalid"),
      HaveYouRegisteredForVATMOSS.options.toSeq.map(_.value): _*
    )
  }
}
