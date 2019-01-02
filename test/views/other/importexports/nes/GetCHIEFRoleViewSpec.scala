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

      view must include("When you get this, sign in and add the New Export System to your account.")

      view must include("Email your business details to chief.operations@hmrc.gsi.gov.uk to request a CHIEF role.")
    }
  }
}
