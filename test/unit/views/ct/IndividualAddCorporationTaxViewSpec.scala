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

package views.ct

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.ct.individual_add_corporation_tax

class IndividualAddCorporationTaxViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "individualAddCorporationTax"

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new individual_add_corporation_tax(formWithCSRF, mainTemplate)(frontendAppConfig)(serviceInfoContent)(fakeRequest, messages)

  "IndividualAddCorporationTaxViewSpec" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "individual-add-ct"
    }
  }

  "IndividualAddCorporationTaxViewSpec" when {
    "rendered" must {
      "contain the correct content" in {
        val view = asDocument(createView()).text()

        view must include("This is an Individual account.")
        view must include("You can only add Corporation Tax to an ‘Organisation’ account.")
      }

      "have the correct links" in {
        val doc = asDocument(createView())

        assertLinkById(
          doc,
          linkId = "not-now",
          expectedText = "I don’t want to do this right now",
          expectedUrl = "http://localhost:9020/business-account"
        )

        assertLinkById(
          doc,
          linkId = "create-account",
          expectedText = "Sign out and create ‘Organisation’ account",
          expectedUrl =
            "http://localhost:9020/business-account/sso-sign-out?continueUrl=%2Fcoafe%2Fgovernment-gateway%2Fregister%" +
              "3FaccountType%3Dorganisation%2526continue%3D%2Fbusiness-account%2Fadd-tax%2526origin%3Dunknown",
          expectedRole = Some("button")
        )

      }
    }
  }

}
