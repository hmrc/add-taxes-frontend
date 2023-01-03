/*
 * Copyright 2023 HM Revenue & Customs
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

import config.FrontendAppConfig
import forms.FormErrorHelper
import forms.mappings.Mappings
import models.sa.KnownFactsNino
import play.api.data.Form
import play.api.data.Forms._
import utils.KnownFactsFormValidator

import javax.inject.Inject

class KnownFactsNinoFormProvider @Inject()(knownFactsFormValidator: KnownFactsFormValidator,
                                           implicit val appConfig: FrontendAppConfig) extends FormErrorHelper with Mappings {

  def apply(): Form[KnownFactsNino] = Form(
    mapping(
      "nino" -> of(knownFactsFormValidator.ninoFormatter(
        ninoKey = "nino",
        blankMessageKey = "enterKnownFacts.nino.error.required",
        lengthMessageKey = "enterKnownFacts.nino.error.length",
        formatMessageKey = "enterKnownFacts.nino.error.format"
      ))
    )(KnownFactsNino.apply)(KnownFactsNino.unapply)
  )
}
