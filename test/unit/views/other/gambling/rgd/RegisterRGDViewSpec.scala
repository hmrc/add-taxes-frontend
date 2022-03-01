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

package views.other.gambling.rgd

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.gambling.rgd.registerRGD

class RegisterRGDViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerRGD"

  def createView: () => HtmlFormat.Appendable = () =>
    new registerRGD(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterRGD view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "register-rgd"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("When you get your registration number, sign in to your account and add Remote Gaming Duty.")

      assertLinkById(
        doc,
        "continue",
        "Register for the Remote Gaming Duty",
        "https://foo.hmrc.gov.uk/gts-registration",
        expectedRole = Some("button")
      )
    }
  }
}
