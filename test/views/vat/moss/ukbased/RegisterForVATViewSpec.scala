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

package views.vat.moss.ukbased

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.ukbased.registerForVAT

class RegisterForVATViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerForVAT"

  def createView = () => registerForVAT(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterForVAT view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "register-for-vat-moss-uk"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Register for VAT",
        "http://localhost:8080/portal/business-registration/introduction?lang=eng",
        "VatMossUkNotVatRegistered:Click:Register",
        expectedRole = Some("button")
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to register for VAT now",
        "http://localhost:9020/business-account",
        "VatMossUkNotVatRegistered:Click:NotNow"
      )

      view must include("You must be VAT registered before you can register for VAT MOSS.")
      view must include(
        "Come back to this account and add VAT MOSS once you’ve received a VAT registration number (VRN).")
    }
  }
}
