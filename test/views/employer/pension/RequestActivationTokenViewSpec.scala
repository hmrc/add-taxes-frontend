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

package views.employer.pension

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.pension.requestActivationToken

class RequestActivationTokenViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "requestActivationToken"

  def createView = () => requestActivationToken(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RequestActivationToken view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "request-activation-token-employer-pension"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "We will send it in the post within 7 days."
      )

      view must include(
        "When you get it, you can sign in to your account and use this service."
      )

      assertLinkById(
        doc,
        "continue",
        "Continue",
        "http://localhost:9555/enrolment-management-frontend/HMRC-PP-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
        "RequestActivationToken:Click:Continue"
      )
    }
  }
}
