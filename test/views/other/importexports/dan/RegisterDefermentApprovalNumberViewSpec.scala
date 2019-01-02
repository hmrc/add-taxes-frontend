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

package views.other.importexports.dan

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.importexports.dan.registerDefermentApprovalNumber

class RegisterDefermentApprovalNumberViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerDefermentApprovalNumber"

  def createView = () => registerDefermentApprovalNumber(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterDefermentApprovalNumber view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "RegisterDefermentApprovalNumber view" should {
    "have the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "When you get this, sign in and add Duty Deferment Electronic Statements to your account"
      )

      assertLinkById(
        doc,
        "register-for-ddes",
        "Apply for deferment approval",
        "https://www.gov.uk/government/publications/notice-101-deferring-duty-vat-and-other-charges/notice-101-deferring-duty-vat-and-other-charges#deferment-approval",
        "RegisterDDES:Click:Continue"
      )
    }
  }

}
