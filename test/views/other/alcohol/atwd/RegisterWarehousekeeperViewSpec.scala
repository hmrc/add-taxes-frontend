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

package views.other.alcohol.atwd

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.alcohol.atwd.registerWarehousekeeper

class RegisterWarehousekeeperViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerWarehousekeeper"

  def createView = () => registerWarehousekeeper(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterWarehousekeeper view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You need to register as an excise warehousekeeper before you can add ATWD to your account.")

      view must include("We’ll send you a warehouse ID. Come back to your account and add ATWD when you get it.")

      assertLinkById(
        doc,
        "continue",
        "Continue - register as an excise warehousekeeper",
        "https://www.gov.uk/government/publications/excise-warehousing-application-to-be-an-excise-warehousekeeper-ex61",
        "RegisterAtwd:Click:Continue"
      )
    }
  }
}
