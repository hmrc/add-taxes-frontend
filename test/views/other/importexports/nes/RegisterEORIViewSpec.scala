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

package views.other.importexports.nes

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.importexports.nes.registerEORI

class RegisterEORIViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerEORI"

  def createView = () => registerEORI(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterEORI view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "You need an Economic Operators Registration and Identification (EORI) to add National " +
          "Export System (NES) to your account.")

      view must include("Come back to your account and add (NES) once your EORI number arrives.")

      assertLinkById(
        doc,
        "continue",
        "Continue - get an EORI number",
        "https://www.gov.uk/eori#how-to-get-an-eori-number",
        "RegisterNESEori:Click:Continue")
    }
  }
}
