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
import play.api.data.Form
import models.sa.HaveSAUTRModel
import forms.mappings.{Constraints, Mappings}
import play.api.data.Forms.mapping
import forms.mappings.{Constraints, Mappings}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.i18n.Messages

import javax.inject.Inject

class HaveSAUTRFormProvider @Inject() extends FormErrorHelper with Mappings {

  def apply(): Form[HaveSAUTRModel] =
    Form(mapping (
      "value" -> boolean("doYouHaveSAUTR.error.required"),
      "sautrValue" -> optional(text())
  )(HaveSAUTRModel.formApply)(unapply = HaveSAUTRModel.formUnapply).transform[HaveSAUTRModel](
      details => details.copy(
        value = details.value,
        sautrValue = details.sautrValue
      ), x => x
    ).verifying(firstError(
      utrProvidedAndutrChecked
    )
    ))

  def utrProvidedAndutrChecked: Constraint[HaveSAUTRModel] = Constraint {
    model =>
      println("ppppppppppppppppppppppppppp")
      if (model.value && model.sautrValue.isEmpty) {
        println("invalid ppppppppppppppppppppppppppp")

        println(model.value)
        println(model.sautrValue)
        Invalid("enterSAUTR.error.required")
      } else if (model.value && model.sautrValue.isDefined) {
        println("invalid ppppppppppppppppppppppppppp")
        println(model.value)
        println(model.sautrValue)
          if(model.sautrValue.get.length == 13) {
            Valid
          } else {
            Invalid("enterSAUTR.error.length")
          }

        if(model.sautrValue.get.matches("^(\\d)+$")) {
          Valid
        } else {
          Invalid("enterSAUTR.error.characters")
        }
      }
      else {
        println("valid ppppppppppppppppppppppppppp")
        println(model.value)
        println(model.sautrValue)
        Valid
      }
  }
}
