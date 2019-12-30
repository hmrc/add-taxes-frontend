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

package views.vat.moss.eu

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.eu.registerInHomeCountry

class RegisterInHomeCountryViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerInHomeCountry"

  def createView = () => registerInHomeCountry(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterInHomeCountry view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "register-in-home-country"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())

      assertLinkById(
        doc,
        "call-us",
        "call us.",
        "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/vat-enquiries",
        "VatMossEu:Click:CallUs")
      assertContainsText(
        doc,
        "You should register for VAT MOSS with the tax authority in the home country of your business.")
      assertContainsText(doc, "If you want to register for VAT MOSS in the UK, you need to")
    }
  }
}
