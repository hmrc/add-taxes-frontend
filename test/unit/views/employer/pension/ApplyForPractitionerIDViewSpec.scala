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
import views.html.employer.pension.applyForPractitionerID

class ApplyForPractitionerIDViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "applyForPractitionerID"

  def createView: () => HtmlFormat.Appendable = () =>
    new applyForPractitionerID(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "ApplyForPractitionerID view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "apply-for-practitioner-id"
    }

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include(
        "When you get this ID, sign in to your account and add Pension Schemes Online for Practitioners.")

      assertLinkById(
        doc,
        "continue",
        "Apply for a Scheme Practitioner ID",
        "https://pensionschemes.hmrc.gov.uk/pso/reg/registeruserinitialquestions.aspx",
        expectedRole = Some("button")
      )

      assertLinkById(
        doc,
        "not-now",
        "I do not want to do this now",
        "http://localhost:9020/business-account"
      )
    }
  }
}
