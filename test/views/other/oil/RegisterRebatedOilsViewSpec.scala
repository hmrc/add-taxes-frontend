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

package views.other.oil

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.oil.registerRebatedOils

class RegisterRebatedOilsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerRebatedOils"

  def createView = () => registerRebatedOils(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterRebatedOils view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "RegisterRebatedOils view" when {
    "rendered" must {
      "contain correct content" in {
        val doc = asDocument(createView())

        doc.text() must include("When you get an approval number, sign in to your account and add this service.")
      }

      "contain 'Register for the Rebated Oils Enquiry Service' for the button text" in {
        val doc = asDocument(createView())

        doc
          .getElementById("register-for-rebatedoils")
          .text() mustBe "Register for the Rebated Oils Enquiry Service"
      }
    }
  }
}
