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

package views.sa

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.enterSAUTR
import forms.sa.SAUTRFormProvider
import models.sa.SAUTR
import play.api.data.Form

class EnterSAUTRViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "enterSAUTR"

  val form = new SAUTRFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => enterSAUTR(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[SAUTR]) => enterSAUTR(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "EnterSAUTR view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "EnterSAUTR view" when {
    "rendered" must {
      "contain paragraph text" in {
        val doc = asDocument(createViewUsingForm(form))

        assertContainsText(
          doc,
          "You can find this on your tax return, statement of account or any other self-assessment calculations." +
            " It is 10 numbers. For example, 1234567890"
        )
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
