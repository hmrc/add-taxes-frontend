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

package views.vat.vat

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.vat.setupNewAccount

class SetupNewAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "setupNewAccount.vat"

  def createView = () => setupNewAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "SetupNewAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You need to register for VAT and get a VAT registration number (VRN) to manage VAT online.")
      view must include("You can only register for VAT from an ‘Organisation’ account.")

      assertLinkById(
        doc,
        "continue",
        "Sign out and create ‘Organisation’ account",
        "http://localhost:8571/government-gateway-registration-frontend?accountType=organisation&continue=%2Fbusiness-account%2Fadd-tax",
        "VatVat:Click:VatVat"
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this now",
        "http://localhost:9020/business-account",
        "VatVat:Click:NotNow"
      )
    }
  }
}
