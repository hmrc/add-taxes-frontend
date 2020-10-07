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

package views.vat

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.{vatAccountUnavailable}

class VatAccountUnavailableViewSpec extends ViewBehaviours {


  val messageKeyPrefix = "vatAccountUnavailable"

  def createView: () => HtmlFormat.Appendable = () =>
    new vatAccountUnavailable(mainTemplate)(frontendAppConfig)(fakeRequest, messages)

  "vatAccountUnavailable view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "vat-Account-Unavailable"
    }

    "include correct content" in {
      val doc = asDocument(createView())
      assertContainsText(
        doc,
        "You were recently signed up to the new Making Tax Digital service. This is so you can submit your VAT Returns to HMRC using compatible software.")
      assertContainsText(
        doc,
        "It will take up to 72 hours for your account to be set up.")
      assertContainsText(
        doc,
        "Check back again later.")
    }


  }


}
