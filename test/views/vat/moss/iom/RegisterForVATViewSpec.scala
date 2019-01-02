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

package views.vat.moss.iom

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.iom.registerForVAT

class RegisterForVATViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerForVAT"

  def createView = () => registerForVAT(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterForVAT view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You must be VAT registered before you can register for VAT MOSS.")
      view must include(
        "Come back to this account and add VAT MOSS once you’ve received a VAT registration number (VRN).")

      assertLinkById(
        doc,
        "continue",
        "Register for VAT",
        "https://www.gov.im/categories/tax-vat-and-your-money/customs-and-excise/registering-for-vat/",
        "VatMossUkNotVatRegistered:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this now",
        "http://localhost:9020/business-account",
        "VatMossUkNotVatRegistered:Click:NotNow"
      )
    }
  }
}
