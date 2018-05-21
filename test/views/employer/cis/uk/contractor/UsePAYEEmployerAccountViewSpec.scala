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

package views.employer.cis.uk.contractor

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.cis.uk.contractor.usePAYEEmployerAccount

class UsePAYEEmployerAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "usePAYEEmployerAccount"

  def createView = () => usePAYEEmployerAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "UsePAYEEmployerAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {

      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You should manage CIS in the same account that you manage PAYE for employers.")

      view must include("Use your PAYE for employers account")

      view must include("Sign in to your PAYE for employers account")

      view must include("I want to add CIS in this account")

      assertLinkById(
        doc,
        "continue",
        "Sign in to your PAYE for employers account",
        "http://localhost:9020/business-account/sso-sign-out?continueUrl=%2Fbusiness-account",
        "CisUkContractorEpayeOtherAccount:Click:SignIn"
      )

      assertLinkById(
        doc,
        "add-to-this-account",
        "I want to add CIS in this account",
        "http://localhost:9555/enrolment-management-frontend/HMRC-CIS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
        "CisUkContractorEpayeOtherAccount:Click:AddToAccount"
      )
    }
  }
}
