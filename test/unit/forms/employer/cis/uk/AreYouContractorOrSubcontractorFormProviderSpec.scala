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

package forms.employer.cis.uk

import forms.behaviours.FormBehaviours
import models._
import models.employer.cis.uk._

class AreYouContractorOrSubcontractorFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> AreYouContractorOrSubcontractor.options.head.value
  )

  val form = new AreYouContractorOrSubcontractorFormProvider()()

  "AreYouContractorOrSubcontractor form" must {

    behave like questionForm[AreYouContractorOrSubcontractor](AreYouContractorOrSubcontractor.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "areYouContractorOrSubcontractor.error.required", Invalid -> "error.invalid"),
      AreYouContractorOrSubcontractor.options.toSeq.map(_.value): _*
    )
  }
}
