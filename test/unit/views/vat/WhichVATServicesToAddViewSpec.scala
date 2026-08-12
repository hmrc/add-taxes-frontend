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

package views.vat

import forms.vat.WhichVATServicesToAddFormProvider
import models.vat.WhichVATServicesToAdd
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.vat.whichVATServicesToAdd

class WhichVATServicesToAddViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichVATServicesToAdd"

  val form = new WhichVATServicesToAddFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
      new whichVATServicesToAdd(formWithCSRF, mainTemplate)(frontendAppConfig, form, WhichVATServicesToAdd.options, true)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
      new whichVATServicesToAdd(formWithCSRF, mainTemplate)(frontendAppConfig, form, WhichVATServicesToAdd.options, true)(serviceInfoContent)(fakeRequest, messages)

  "WhichVATServicesToAdd view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "which-vat-services-to-add"
    }
  }

  "WhichVATServicesToAdd view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- WhichVATServicesToAdd.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- WhichVATServicesToAdd.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- WhichVATServicesToAdd.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
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
