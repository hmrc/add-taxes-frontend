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

package views.vat

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.cannotRegisterVAT

class CannotRegisterVATViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "cannotRegisterVAT"

  def createView = () => cannotRegisterVAT(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "CannotRegisterVAT view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "include header" in {
      val doc = asDocument(createView())
      assertContainsText(doc, "You must register for VAT by post.")
      assertContainsText(doc, "When you get your VAT number, you can sign in and add the VAT online service.")
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(doc, "continue", "Register for VAT by post", "#", "CannotRegisterVAT:Click:Continue")
    }
  }
}