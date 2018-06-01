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

package views.employer.cis.ukbased

import play.api.data.Form
import forms.employer.cis.uk.AreYouContractorOrSubcontractorFormProvider
import models.employer.cis.uk.AreYouContractorOrSubcontractor
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.cis.ukbased.areYouContractorOrSubcontractor

class AreYouContractorOrSubcontractorViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "areYouContractorOrSubcontractor"

  val form = new AreYouContractorOrSubcontractorFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () => areYouContractorOrSubcontractor(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) =>
      areYouContractorOrSubcontractor(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "AreYouContractorOrSubcontractor view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Are you a contractor or subcontractor?")

      view must include(
        "If you are both, you will need to do this twice. Select an option then come back and select the other.")

      view must include("Contractor, employs and pays subcontractors")

      view must include("Subcontractor, works for other construction businesses")
    }
  }

  "AreYouContractorOrSubcontractor view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- AreYouContractorOrSubcontractor.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- AreYouContractorOrSubcontractor.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- AreYouContractorOrSubcontractor.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))

        assertEqualsMessage(doc, "title", "error.browser.title", messages(s"$messageKeyPrefix.title"))
      }
    }
  }
}
