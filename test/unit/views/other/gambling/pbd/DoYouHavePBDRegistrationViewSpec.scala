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

package views.other.gambling.pbd

import forms.other.gambling.pbd.DoYouHavePBDRegistrationFormProvider
import models.other.gambling.pbd.DoYouHavePBDRegistration
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.other.gambling.pbd.doYouHavePBDRegistration

class DoYouHavePBDRegistrationViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doYouHavePBDRegistration"

  val form = new DoYouHavePBDRegistrationFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new doYouHavePBDRegistration(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new doYouHavePBDRegistration(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "DoYouHavePBDRegistration view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "do-you-have-pbd-registration"
    }
  }

  "DoYouHavePBDRegistration view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouHavePBDRegistration.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- DoYouHavePBDRegistration.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- DoYouHavePBDRegistration.options.filterNot(_ == option)) {
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
