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

package views.vat.moss

import controllers.vat.moss.iom.routes
import forms.vat.moss.AlreadyRegisteredForVATMossFormProvider
import models.vat.moss.AlreadyRegisteredForVATMoss
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import viewmodels.ViewAction
import views.behaviours.ViewBehaviours
import views.html.vat.moss.alreadyRegisteredForVATMoss

class AlreadyRegisteredForVATMossViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "alreadyRegisteredForVATMoss"

  val form = new AlreadyRegisteredForVATMossFormProvider()()
  val viewAction = ViewAction(routes.AlreadyRegisteredForVATMossController.onSubmit(), "VatMossNoVatIomVatRegistered")

  val serviceInfoContent = HtmlFormat.empty

  def createView: () => Html =
    () => alreadyRegisteredForVATMoss(frontendAppConfig, form, viewAction)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => Html =
    (form: Form[_]) =>
      alreadyRegisteredForVATMoss(frontendAppConfig, form, viewAction)(serviceInfoContent)(fakeRequest, messages)

  "AlreadyRegisteredForVATMoss view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "AlreadyRegisteredForVATMoss view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))

        doc.text() must include("You will have a VAT MOSS identification number if you are already registered.")

        for (option <- AlreadyRegisteredForVATMoss.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- AlreadyRegisteredForVATMoss.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- AlreadyRegisteredForVATMoss.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
}
