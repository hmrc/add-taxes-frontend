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

package views.employer.ers

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.ers.registerEmployersPAYE

class RegisterEmployersPAYEViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerEmployersPAYE"

  def createView = () => registerEmployersPAYE(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterEmployersPAYE view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "You need to be registered for PAYE for employers before you can manage employment related securities (ERS) online.")

      view must include(
        "Register for PAYE for employers, then come back and add employment related securities afterwards.")

      assertLinkById(
        doc,
        "continue",
        "Register for PAYE for employers",
        "http://localhost:8080/portal/business-registration/introduction?lang=eng",
        "AddErsRegisterEpaye:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I don’t want to do this right now",
        "http://localhost:9020/business-account",
        "AddErsRegisterEpaye:Click:NotNow"
      )
    }
  }
}
