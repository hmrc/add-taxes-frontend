/*
 * Copyright 2020 HM Revenue & Customs
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

package views.vat.giant

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.giant.youDoNotNeedVAT

class YouDoNotNeedVATViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "youDoNotNeedVAT"

  def createView = () => youDoNotNeedVAT(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "YouDoNotNeedVAT view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "do-not-need-vat-giant"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("HMRC will contact your organisation if it needs to use this service.")

      assertLinkById(
        doc,
        "not-now",
        "Go to business tax home",
        "http://localhost:9020/business-account",
        "VatGiant:Click:NotNow"
      )
    }
  }
}
