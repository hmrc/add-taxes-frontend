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

package views.other.land.stampduty

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.land.stampduty.paperForms

class PaperFormsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "paperForms"

  def createView = () => paperForms(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "PaperForms view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "paper-forms"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("This is only for professionals acting on behalf of clients.")
      view must include(
        "You need to complete paper forms for Stamp Duty if you are purchasing the land or property for yourself.")

      assertLinkById(
        doc,
        "continue",
        "Fill in paper forms",
        "https://www.gov.uk/guidance/sdlt-completing-the-paper-return",
        "RegisterSdltPaperForms:Click:Continue"
      )
    }
  }
}
