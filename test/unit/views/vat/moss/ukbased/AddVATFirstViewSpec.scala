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

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.ukbased.addVATFirst

class AddVATFirstViewSpec extends ViewBehaviours with MockitoSugar with BeforeAndAfterEach {

  val messageKeyPrefix = "addVATFirst"

  def createView: () => HtmlFormat.Appendable = () =>
    new addVATFirst(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "AddVATFirst view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "add-vat-first"
    }

    "Render the correct content" in {

      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Add VAT to this account",
        "http://localhost:9555/enrolment-management-frontend/HMCE-VATDEC-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
        "VatMossUkAddVatToAccount:Click:AddVat",
        expectedRole = Some("button")
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to add VAT to this account now",
        "http://localhost:9020/business-account",
        "VatMossUkAddVatToAccount:Click:NotNow"
      )
      view must include("You need to manage VAT online before you can add VAT MOSS to your account.")
    }
  }
}
