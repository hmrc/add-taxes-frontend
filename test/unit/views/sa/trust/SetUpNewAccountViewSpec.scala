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

package views.sa.trust

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.trust.setUpNewAccount

class SetUpNewAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "setUpNewAccount"

  def createView: () => HtmlFormat.Appendable = () =>
    new setUpNewAccount(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "SetUpNewAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "setup-new-account-sa-trust"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You can’t add a Self Assessment Trust to this account.")

      view must include("You need to:")

      view must include("Create an Organisation account on the Government Gateway website.")

      view must include("Sign in to HMRC with the new sign in details.")

      view must include("Add a Self Assessment Trust in that account.")

      view must include("Don’t try to add a Self Assessment Trust on the Government Gateway website.")

      view must include("Sign out and create ‘Organisation’ account")

      assertLinkById(
        doc = doc,
        linkId = "continue",
        expectedText = "Sign out and create ‘Organisation’ account",
        expectedUrl = signOutAndRegisterAsOrgLink,
        expectedRole = Some("button")
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this now",
        "http://localhost:9020/business-account"
      )
    }
  }
}
