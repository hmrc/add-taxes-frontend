/*
 * Copyright 2022 HM Revenue & Customs
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

import forms.FormErrorHelper
import forms.mappings.Mappings
import javax.inject.Inject
import models.employer.paye.{PAYEReference, PAYEReference_}
import play.api.data.Form
import play.api.data.Forms._

class WhatIsYourPAYEReferenceFormProvider @Inject() extends FormErrorHelper with Mappings {
  def apply(): Form[PAYEReference] = Form(
    mapping(
      "empRef" -> text("whatIsYourPAYEReference.error.required")
        .verifying(regexp("^\\d{3}\\/[A-Za-z0-9]{1,10}$", "whatIsYourPAYEReference.error.format"))
    )(PAYEReference_.apply)(PAYEReference_.unapply)
  )
}
