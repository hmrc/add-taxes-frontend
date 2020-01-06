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

package views.other.charity

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.charity.registerForCharity

class RegisterForCharityViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerForCharity"

  def createView = () => registerForCharity(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterForCharity view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "register-for-charity"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You need to register with HMRC as a charity before you add this service to your account")
      view must include("When you get the reference, sign in and add this service.")

      assertLinkById(
        doc,
        "continue",
        "Register with HMRC as a charity",
        "https://www.gov.uk/charity-recognition-hmrc",
        "RegisterCharities:Click:Continue",
        expectedRole = Some("button")
      )
    }
  }
}
