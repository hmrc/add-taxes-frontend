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

package views.vat.moss.newaccount

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.newaccount.setUpANewAccount

class SetUpANewAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "setUpANewAccount"

  def createView = () => setUpANewAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "SetUpANewAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain subtext 1" in {
      val view = createView()
      val doc = asDocument(view).text()

      doc must include("This is an Individual account.")
    }

    "contain subtext 2" in {
      val view = createView()
      val doc = asDocument(view).text()

      doc must include("You can only add VAT MOSS to an ’Organisation’ account.")
    }

    "contain subtext 3" in {
      val view = createView()
      val doc = asDocument(view).text()

      doc must include("Call us and we’ll help you do this over the phone.")
    }

    "link contains href for call us " in {
      val view = createView()
      val doc = asDocument(view)

      assertLinkByContent(
        doc,
        "Call us",
        "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/online-services-helpdesk")
    }
  }

}
