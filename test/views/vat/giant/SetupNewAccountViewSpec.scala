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

package views.vat.giant

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.giant.setupNewAccount

class SetupNewAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "setupNewAccount"

  def createView = () => setupNewAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "SetupNewAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "setup-new-account-vat-giant"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("This is an Individual account.")
      view must include("You can only add VAT GIANT to an ‘Organisation’ account.")

      assertLinkById(
        doc,
        "continue",
        "Sign out and create ‘Organisation’ account",
        "http://localhost:8571/government-gateway-registration-frontend?accountType=organisation&continue=%2Fbusiness-account%2Fadd-tax",
        "VatGiant:Click:SignOut",
        expectedRole = Some("button")
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this now",
        "http://localhost:9020/business-account",
        "VatGiant:Click:NotNow"
      )
    }
  }
}
