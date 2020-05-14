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

package views.other.ctf

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.ctf.youNeedToBeApprovedCTF

class YouNeedToBeApprovedCTFViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "youNeedToBeApprovedCTF"

  def createView: () => HtmlFormat.Appendable = () =>
    new youNeedToBeApprovedCTF(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "YouNeedToBeApprovedCTF view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "you-need-to-be-approved-ctf"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "When you get your reference number and activation token, sign in to your account and add Child Trust Fund.")

      assertLinkById(
        doc,
        "continue",
        "Apply to become an approved provider",
        "https://www.gov.uk/government/publications/child-trust-fund-apply-to-become-an-approved-provider-ctf11app",
        "CtfApply:Click:Continue",
        expectedRole = Some("button")
      )
    }
  }
}
