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

package views.vat

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.vatEligibilityCheck

class VatEligibilityCheckViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "vatEligibilityCheck"

  def createView: () => HtmlFormat.Appendable = () =>
    new vatEligibilityCheck(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "VatEligibilityCheck view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include correct content" in {
      val doc = asDocument(createView())
      assertContainsText(
        doc,
        "You will be asked up to 6 questions to check if you are eligible to register for VAT online.")
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Continue",
        "/business-account/add-tax/vat/has-vat-exception",
        expectedRole = Some("button"))
    }
  }
}
