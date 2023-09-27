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

package views.employer.cis.ukbased.contractor

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.cis.ukbased.contractor.usePAYEEmployerAccount

class UsePAYEEmployerAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "usePAYEEmployerAccount"

  def createView: () => HtmlFormat.Appendable = () =>
    new usePAYEEmployerAccount(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "UsePAYEEmployerAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "use-paye-employer-account-cis-uk"
    }

    "Render the correct content" in {

      val doc = asDocument(createView())
      val view = doc.text()

      view must include("Add the Construction Industry Scheme (CIS) to the account you use for PAYE for employers")

      view must include("Sign in to your other account and add the CIS.")

      assertLinkById(
        doc,
        "continue",
        "Sign in to your other account",
        "http://localhost:9020/business-account/sso-sign-out?continueUrl=http://localhost:9553/business-account",
        expectedRole = Some("button")
      )

      assertLinkById(
        doc,
        "add-to-this-account",
        "I want to add CIS to this account",
        "http://localhost:9555/enrolment-management-frontend/HMRC-CIS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
      )
    }
  }
}
