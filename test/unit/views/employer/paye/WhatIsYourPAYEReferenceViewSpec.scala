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

package views.employer.paye

import forms.employer.paye.WhatIsYourPAYEReferenceFormProvider
import models.employer.paye.PAYEReference
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.employer.paye.whatIsYourPAYEReference

class WhatIsYourPAYEReferenceViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whatIsYourPAYEReference"

  val form = new WhatIsYourPAYEReferenceFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new whatIsYourPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages)

  def createViewUsingForm: Form[PAYEReference] => HtmlFormat.Appendable = (form: Form[PAYEReference]) =>
    new whatIsYourPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "whatIsYourPAYEReference view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "whatIsYourPAYEReference view" when {
    "rendered with no errors" must {
      val doc = asDocument(createViewUsingForm(form))

      "have the correct Browser title" in {
        doc.title mustBe "What is your employer PAYE reference? - Business tax account - GOV.UK"
      }

      "contain the correct heading" in {
        doc.select("h1").size mustBe 1
        doc.select("h1").text mustBe "What is your employer PAYE reference?"
      }

      "contain an input field for HMRC office number" which {
        "has the correct name" in {
          doc.select("input#empRef").attr("name") mustBe "empRef"
        }

        "has an associated label" in {
          val label = doc.select("h1 > label")
          label.attr("for") mustBe "empRef"
          label.text mustBe "What is your employer PAYE reference?"
        }

        "has associated hint text" in {
          doc.select("#hint-text").text mustBe "It is on letters from HMRC about PAYE. For example, 123/A246. " +
            "You may need to enter this reference again on the next screen, to verify your request."
          doc.select("input#empRef").attr("aria-describedby") mustBe "hint-text"
        }
      }

      "have a continue button" in {
        doc.getElementById("continue-button").text mustBe "Continue"
      }

    }
  }

  "whatIsYourPAYEReference view" when {
    "when the form fields are left empty" must {
      val doc = asDocument(createViewUsingForm(form.bind(Map("empRef" -> ""))))

      "Append 'Error: ' to the Browser title" in {
        doc.title mustBe "Error: What is your employer PAYE reference? - Business tax account - GOV.UK"
      }

      "Display an error summary" in {
        doc.select("div.govuk-error-summary").size mustBe 1
      }

      "Have the 'There is a problem' heading" in {
        doc.getElementById("error-summary-heading").text mustBe "There is a problem"
      }

      "Contain error messages in the error summary" in {
        val error = doc.select("ul.govuk-error-summary__list > li > a")
        error.attr("href") mustBe "#empRef"
        error.text mustBe "Enter your employer PAYE reference"
      }

      "Contain error messages at the input fields" in {
        doc.getElementById("error-message-empRef-input").text mustBe "Error: Enter your employer PAYE reference"
      }
    }
  }

  "whatIsYourPAYEReference view" when {
    "when invalid data is submitted" must {
      val doc = asDocument(createViewUsingForm(form.bind(Map("empRef" -> "hjdsA$^G2"))))

      "Append 'Error: ' to the Browser title" in {
        doc.title mustBe "Error: What is your employer PAYE reference? - Business tax account - GOV.UK"
      }

      "Display an error summary" in {
        doc.select("div.govuk-error-summary").size mustBe 1
      }

      "Have the 'There is a problem' heading" in {
        doc.getElementById("error-summary-heading").text mustBe "There is a problem"
      }

      "Contain error messages in the error summary" in {
        val error = doc.select("ul.govuk-error-summary__list > li > a")
        error.attr("href") mustBe "#empRef"
        error.text mustBe "Enter your employer PAYE reference in the correct format"
      }

      "Contain error messages at the input fields" in {
        doc.getElementById("error-message-empRef-input").text mustBe "Error: Enter your employer PAYE reference in the correct format"
      }
    }
  }
}
