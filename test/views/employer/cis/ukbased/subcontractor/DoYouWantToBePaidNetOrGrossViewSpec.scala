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

package views.employer.cis.ukbased.subcontractor

import play.api.data.Form
import forms.employer.cis.uk.subcontractor.DoYouWantToBePaidNetOrGrossFormProvider
import models.employer.cis.uk.subcontractor.DoYouWantToBePaidNetOrGross
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.cis.ukbased.subcontractor.doYouWantToBePaidNetOrGross

class DoYouWantToBePaidNetOrGrossViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "doYouWantToBePaidNetOrGross"

  val form = new DoYouWantToBePaidNetOrGrossFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => doYouWantToBePaidNetOrGross(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => doYouWantToBePaidNetOrGross(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "DoYouWantToBePaidNetOrGross view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain the subtext1" in {
      val doc = asDocument(createView())

      assertContainsText(
        doc,
        "If you’re paid net, 20% of your pay will be deducted to cover tax and National Insurance.")
    }

    "contain the subtext2" in {
      val doc = asDocument(createView()).text()

      doc must include(
        "If you want to be paid gross, you must check if you qualify for gross payments before you continue.")
    }

    "contains check if you qualify for gross payment - link" in {
      val doc = asDocument(createView())
      assertLinkById(
        doc,
        "CISNetOrGross",
        "check if you qualify for gross payments",
        "https://www.gov.uk/what-you-must-do-as-a-cis-subcontractor/gross-payment-status",
        "CISNetOrGross:Click:CheckQualified"
      )
    }
  }

  "DoYouWantToBePaidNetOrGross view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouWantToBePaidNetOrGross.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- DoYouWantToBePaidNetOrGross.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- DoYouWantToBePaidNetOrGross.options.filterNot(o => o == option)) {
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
