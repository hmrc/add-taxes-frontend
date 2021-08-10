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

import forms.employer.paye.PAYEReferenceFormProvider
import models.employer.paye.PAYEReference
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.employer.paye.enterPAYEReference

class EnterPAYEReferenceViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "enterYourPAYEReference"

  val form = new PAYEReferenceFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new enterPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages)

  def createViewUsingForm: Form[PAYEReference] => HtmlFormat.Appendable = (form: Form[PAYEReference]) =>
    new enterPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "enterYourPAYEReference view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "enterYourPAYEReference view" when {
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
          doc.select("input#officeNumber").attr("name") mustBe "officeNumber"
        }

        "has an associated label" in {
          val label = doc.select("div.form-group > label").get(0)
          label.attr("for") mustBe "officeNumber"
          label.text mustBe "HMRC office number"
        }

        "has associated hint text" in {
          doc.select("#hint-text-officeNumber").text mustBe "It is on the letter HMRC sent you when you registered " +
            "as an employer. Enter the first 3 numbers. For example, 123/A246, enter 123"
          doc.select("input#officeNumber").attr("aria-describedby") mustBe "hint-text-officeNumber"
        }
      }

      "contain an input field for Employer PAYE reference" which {
        "has the correct name" in {
          doc.select("input#payeReference").attr("name") mustBe "payeReference"
        }

        "has an associated label" in {
          val label = doc.select("div.form-group > label").get(1)
          label.attr("for") mustBe "payeReference"
          label.text mustBe "Employer PAYE reference"
        }

        "has associated hint text" in {
          doc.select("#hint-text-payeReference").text mustBe "Enter the second part of your reference " +
            "(any letters and numbers after the slash). For example, A246"
          doc.select("input#payeReference").attr("aria-describedby") mustBe "hint-text-payeReference"
        }
      }

      "have a continue button" in {
        doc.getElementById("continue-button").text mustBe "Continue"
      }

    }
  }

  "enterYourPAYEReference view" when {
    "when the form fields are left empty" must {
      val doc = asDocument(createViewUsingForm(form.bind(Map("officeNumber" -> "", "payeReference" -> ""))))

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
        val errors = doc.select("ul.govuk-error-summary__list > li > a")
        errors.get(0).attr("href") mustBe "#officeNumber"
        errors.get(1).attr("href") mustBe "#payeReference"
        errors.get(0).text mustBe "Enter your HMRC office number"
        errors.get(1).text mustBe "Enter your employer PAYE reference"
      }

      "Contain error messages at the input fields" in {
        doc.getElementById("error-message-officeNumber-input").text mustBe "Error: Enter your HMRC office number"
        doc.getElementById("error-message-payeReference-input").text mustBe "Error: Enter your employer PAYE reference"
      }
    }
  }

  "enterYourPAYEReference view" when {
    "when invalid data is submitted" must {
      val doc = asDocument(createViewUsingForm(form.bind(Map("officeNumber" -> "hjds", "payeReference" -> ":_+\""))))

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
        val errors = doc.select("ul.govuk-error-summary__list > li > a")
        errors.get(0).attr("href") mustBe "#officeNumber"
        errors.get(1).attr("href") mustBe "#payeReference"
        errors.get(0).text mustBe "Enter your HMRC office number in the correct format"
        errors.get(1).text mustBe "Enter your employer PAYE reference in the correct format"
      }

      "Contain error messages at the input fields" in {
        doc.getElementById("error-message-officeNumber-input").text mustBe "Error: Enter your HMRC office number in the correct format"
        doc.getElementById("error-message-payeReference-input").text mustBe "Error: Enter your employer PAYE reference in the correct format"
      }
    }
  }
}
