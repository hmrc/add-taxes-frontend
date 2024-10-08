/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.FormErrorHelper
import forms.mappings.Mappings
import models.sa.{CaptureSAUTRModel, DoYouHaveSAUTR}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid,ValidationError}
import javax.inject.Inject
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual


class CaptureSAUTRFormProvider @Inject() extends FormErrorHelper with Mappings {

  def apply(): Form[CaptureSAUTRModel] = {
    Form(mapping (
      "value" -> enumerable[DoYouHaveSAUTR]("doYouHaveSAUTR.error.required"),
      "sautrValue" -> mandatoryIfEqual("value", DoYouHaveSAUTR.Yes.toString,
        text("enterSAUTR.error.required")
          .verifying(length(10, "enterSAUTR.error.length"))
           .verifying(regexp("^(\\d)+$", "enterSAUTR.error.characters"))
      )
    )(CaptureSAUTRModel.formApply)(CaptureSAUTRModel.formUnapply)
    )
  }

}