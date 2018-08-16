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

package views.sa.partnership

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.partnership.setUpNewAccount

class SetUpNewAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "setUpNewAccount"

  def createView = () => setUpNewAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "SetUpNewAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You can’t add a Self Assessment partnership to this account.")

      view must include("You need to:")

      view must include("Create an Organisation account on the Government Gateway website.")

      view must include("Sign in to HMRC with the new sign in details.")

      view must include("Add a Self Assessment partnership in that account.")

      view must include("Don’t try to add a Self Assessment partnership on the Government Gateway website.")

      view must include("Sign out and create ‘Organisation’ account")

      assertLinkById(
        doc,
        "continue",
        "Sign out and create ‘Organisation’ account",
        "http://localhost:9020/business-account/sso-sign-out?continueUrl=%2Fcoafe%2Fgovernment-gateway%2Fregister%3FaccountType%3Dorganisation%2526continue%3D%2Fbusiness-account%2Fadd-tax%2526origin%3Dunknown",
        "SetUpAccountaSelfAssessmentpartnership:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this right now",
        "http://localhost:9020/business-account",
        "SetUpAccountaSelfAssessmentpartnership:Click:NotNow"
      )
    }
  }
}
