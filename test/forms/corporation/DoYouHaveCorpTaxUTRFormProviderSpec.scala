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

package forms.corporation

import forms.behaviours.FormBehaviours
import models._
import models.corporation._

class DoYouHaveCorpTaxUTRFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> DoYouHaveCorpTaxUTR.options.head.value
  )

  val form = new DoYouHaveCorpTaxUTRFormProvider()()

  "DoYouHaveCorpTaxUTR form" must {

    behave like questionForm[DoYouHaveCorpTaxUTR](DoYouHaveCorpTaxUTR.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "doYouHaveCorpTaxUTR.error.required", Invalid -> "error.invalid"),
      DoYouHaveCorpTaxUTR.options.toSeq.map(_.value): _*)
  }
}
