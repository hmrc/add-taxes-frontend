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

package views

import forms.OtherTaxesFormProvider
import models.OtherTaxes
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.otherTaxes
import scala.collection.JavaConverters._

class OtherTaxesViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "otherTaxes"

  val form = new OtherTaxesFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView =
    () => otherTaxes(frontendAppConfig, form, OtherTaxes.options)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) =>
      otherTaxes(frontendAppConfig, form, OtherTaxes.options)(serviceInfoContent)(fakeRequest, messages)

  "OtherTaxes view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "OtherTaxes view" when {
    "rendered" must {
      "contain correct content" in {
        val doc = asDocument(createView())
        doc.getElementsByTag("h1").first().text() mustBe "Select a category"

        val listOfOptions: List[String] = List(
          "Alcohol and tobacco wholesaling and warehousing",
          "Automatic Exchange of Information (AEOI)",
          "Charities - for Gift Aid repayment claims",
          "Betting and gaming",
          "Child Trust Fund for providers",
          "Housing and land",
          "Imports and exports",
          "Oil and fuel",
          "Fulfilment House Due Diligence Scheme",
          "Register as a pension scheme administrator"
        )

        val listOfElements =
          doc.getElementsByClass("form-group").first().getElementsByClass("multiple-choice").asScala.toList

        listOfElements.zip(listOfOptions).foreach {
          case (element, answer) => element.text mustBe answer
        }
      }

      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- OtherTaxes.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- OtherTaxes.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- OtherTaxes.options.filterNot(o => o == option)) {
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
