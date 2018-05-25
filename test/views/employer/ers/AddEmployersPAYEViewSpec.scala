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
import views.html.employer.ers.addEmployersPAYE

class AddEmployersPAYEViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "addEmployersPAYE"

  def createView = () => addEmployersPAYE(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "AddEmployersPAYE view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "You have to manage PAYE for employers online before you can manage employment related securities (ERS).")

      view must include(
        "Add PAYE for employers to this account, then come back and add employment related securities (ERS) afterwards.")

      assertLinkById(
        doc,
        "continue",
        "Add PAYE for employers",
        "http://localhost:9555/enrolment-management-frontend/IR-PAYE/request-access-tax-scheme?continue=%2Fbusiness-account",
        "AddErsNotEnrolledEpaye:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I don’t want to do this right now",
        "http://localhost:9020/business-account",
        "AddErsNotEnrolledEpaye:Click:NotNow"
      )
    }
  }
}
