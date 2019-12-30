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

package views.other.gambling.gbd

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.gambling.gbd.registerGBD

class RegisterGBDViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerGBD"

  def createView = () => registerGBD(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterGBD view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "register-gbd"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("When you get your registration number, sign in to your account and add General Betting Duty.")

      assertLinkById(
        doc,
        "continue",
        "Register for the General Betting Duty",
        "https://foo.hmrc.gov.uk/gts-registration",
        "GamblingGBD:Click:Register")
    }
  }
}
