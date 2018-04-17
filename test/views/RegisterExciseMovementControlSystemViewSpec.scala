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

package views

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.registerExciseMovementControlSystem

class RegisterExciseMovementControlSystemViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerExciseMovementControlSystem"

  def createView = () => registerExciseMovementControlSystem(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterExciseMovementControlSystem view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "RegisterExciseMovementControlSystem view" should {
    "have the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "You need to register for a SEED excise ID number before you can " +
          "add Excise Movement and Control System (EMCS) to your account.")

      view must include("Come back to your account and add EMCS once your SEED number arrives.")

      assertLinkById(
        doc,
        "register-for-emcs",
        "Continue - get a SEED number",
        "https://www.gov.uk/guidance/excise-movement-and-control-system-how-to-register-and-use#register-and-enrol",
        "RegisterEMCS:Click:Continue")
    }
  }
}
