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

package views.other.aeio.register

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.aeio.register.registerAEOI

class RegisterAEOIViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerAEOI"

  def createView: () => HtmlFormat.Appendable = () =>
    new registerAEOI(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterAEOI view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "register-aeoi"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Register for AEOI",
        "http://localhost:8080/portal/fatca-registration?lang=eng",
        expectedRole = Some("button")
      )

      assertContainsText(doc, "When you get your AEOI ID, sign in and add this service.")
    }
  }
}
