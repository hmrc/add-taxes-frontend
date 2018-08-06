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

package views.employer.intermediaries

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.intermediaries.usePAYEEmployerAccount

class UsePAYEEmployerAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "usePAYEEmployerIntermediariesAccount"

  def createView = () => usePAYEEmployerAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "UsePAYEEmployerAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("It will make it easier to manage both schemes.")

      view must include("Sign in to your other account and add employment intermediaries.")

      assertLinkById(
        doc,
        "continue",
        "Sign in to the account you use for PAYE for employers",
        "http://localhost:9020/business-account/sso-sign-out?continueUrl=%2Fbusiness-account",
        "AddIntermediariesOtherAccount:Click:SignIn"
      )

      assertLinkById(
        doc,
        "sign-in",
        "I want to add intermediaries to this account",
        "/employment-intermediary-report/not-enrolled",
        "AddIntermediariesOtherAccount:Click:AddToAccount"
      )
    }
  }
}
