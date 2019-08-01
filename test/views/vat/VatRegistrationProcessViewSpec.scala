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

package views.vat

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.vatRegistrationProcess

class VatRegistrationProcessViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "vatRegistrationProcess"

  def createView(): HtmlFormat.Appendable =
    vatRegistrationProcess(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "VATRegistrationProcess view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include list item 1" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Check if you can register for VAT using the online service")
    }
    "include list item 2" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Complete VAT registration")
    }
    "include list item 3" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Get your VAT registration number by post or email reminder")
    }
    "include list item 4" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "Sign into your business tax account to get online access to VAT")
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      assertLinkById(
        doc,
        "continue",
        "Start VAT registration process",
        "/business-account/add-tax/vat/eligibility",
        "VatRegistrationProcess:Click:Continue"
      )
    }
  }
}
