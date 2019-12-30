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

package views.vat.moss.ukbased

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.ukbased.addVATMOSS

class AddVATMOSSViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "addVATMOSS"

  def createView = () => addVATMOSS(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "AddVATMOSS view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "add-vat-moss"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Sign in to your VAT account",
        "http://localhost:9020/business-account/sso-sign-out?continueUrl=%2Fbusiness-account",
        "VatMossUkAddToOtherAccount:Click:SignIn"
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this now",
        "http://localhost:9020/business-account",
        "VatMossUkAddToOtherAccount:Click:NotNow"
      )

      view must include("You need to manage VAT MOSS in the account where you manage VAT.")
      view must include("Sign in to your VAT account and add VAT MOSS there.")
      view must include("If that doesn’t work, call us and we’ll do it for you manually.")
    }
  }
}
