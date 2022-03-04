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

package views.sa

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.sa.enterSAUTR
import forms.sa.SAUTRFormProvider
import models.sa.SAUTR
import play.api.data.Form

class EnterSAUTRViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "enterSAUTR"

  val form = new SAUTRFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty
  val btaOrigin: String = "bta-sa"

  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new enterSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(HtmlFormat.empty)(fakeRequest, messages)

  def createViewUsingForm: (Form[SAUTR], String) => HtmlFormat.Appendable = (form: Form[SAUTR], origin: String) =>
    new enterSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form, origin)(serviceInfoContent)(fakeRequest, messages)


  "EnterSAUTR view" when {
    "rendered" must {
      "contain paragraph text" in {
        val doc = asDocument(createViewUsingForm(form, btaOrigin))

        assertContainsText(
          doc,
          "This 10-digit number is on your tax return or other self assessment documents. For example, 1234567890."
        )
      }

      "contain heading id" in {
        val doc = asDocument(createViewUsingForm(form, btaOrigin))
        doc.getElementsByTag("h1").attr("id") mustBe "enter-sa-utr-heading"
      }
    }
  }

  "invalid data is sent" must {
    "prepend title with Error: " in {
      val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> "")), btaOrigin))
      val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

      assertEqualsMessage(doc, "title", "error.browser.title", title)
    }

    "show error summary when there is error " in {
      val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> "")), btaOrigin))

      assertRenderedById(doc, "error-summary-heading")
    }
  }
}
