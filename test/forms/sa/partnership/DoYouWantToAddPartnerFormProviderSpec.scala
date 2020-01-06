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

package forms.sa.partnership

import forms.behaviours.FormBehaviours
import models._
import models.sa.partnership._

class DoYouWantToAddPartnerFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> DoYouWantToAddPartner.options.head.value
  )

  val form = new DoYouWantToAddPartnerFormProvider()()

  "DoYouWantToAddPartner form" must {

    behave like questionForm[DoYouWantToAddPartner](DoYouWantToAddPartner.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "doYouWantToAddPartner.error.required", Invalid -> "error.invalid"),
      DoYouWantToAddPartner.options.toSeq.map(_.value): _*)
  }
}
