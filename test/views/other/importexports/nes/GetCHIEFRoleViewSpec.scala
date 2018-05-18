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
import views.html.other.importexports.nes.getCHIEFRole

class GetCHIEFRoleViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "getCHIEFRole"

  def createView = () => getCHIEFRole(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "GetCHIEFRole view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You need a CHIEF role to add National Export System (NES) to your account.")

      view must include("Email chief.operations@hmrc.gsi.gov.uk your business details to request a CHIEF role.")

      view must include("Come back to your account and add NES once your CHIEF role arrives")

    }
  }
}
