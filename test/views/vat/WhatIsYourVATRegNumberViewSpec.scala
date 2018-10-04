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

package views.vat

import forms.vat.WhatIsYourVATRegNumberFormProvider
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.vat.whatIsYourVATRegNumber

class WhatIsYourVATRegNumberViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whatIsYourVATRegNumber"

  val form = new WhatIsYourVATRegNumberFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable =
    () => whatIsYourVATRegNumber(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) => whatIsYourVATRegNumber(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "WhatIsYourVATRegNumber view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "WhatIsYourVATRegNumber view" when {
    "rendered" must {
      "contain a text box for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        assertRenderedByCssSelector(doc, ".form-field")
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
