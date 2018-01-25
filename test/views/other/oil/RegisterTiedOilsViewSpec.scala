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
import views.html.other.oil.registerTiedOils

class RegisterTiedOilsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerTiedOils"

  def createView = () => registerTiedOils(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterTiedOils view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "RegisterTiedOils view" when {
    "rendered" must {
      "contain 'You need to register for Tied Oils Enquiry Service before you can add it to your account.' for the first paragraph" in {
        val doc = asDocument(createView())

        doc.getElementById("hint-message").text() mustBe "You need to register for Tied Oils Enquiry Service before you can add it to your account."
      }

      "contain 'We’ll send you an approval number, which you can use to add Tied Oils Enquiry Service to the account.' for the second paragraph" in {
        val doc = asDocument(createView())

        doc.getElementById("hint-message-2").text() mustBe "We’ll send you an approval number, which you can use to add Tied Oils Enquiry Service to the account."
      }

      "contain 'Continue - register for Tied Oils Enquiry Service' for the button text" in {
        val doc = asDocument(createView())

        doc.getElementById("register-for-tiedoils").text() mustBe "Continue - register for Tied Oils Enquiry Service"
      }
    }
  }
}
