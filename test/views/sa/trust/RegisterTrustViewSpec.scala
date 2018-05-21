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

package views.sa.trust

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.trust.registerTrust

class RegisterTrustViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerTrust"

  def createView = () => registerTrust(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterTrust view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      view must include("You need to register your trust before you can add it to this account.")

      view must include(
        "We’ll send you a Unique Taxpayer Reference (UTR) for the trust in the post once it’s registered.")

      view must include("You’ll be able to add the trust to this account using the UTR.")

      assertLinkById(
        doc,
        "continue",
        "Register your trust",
        "https://www.gov.uk/government/publications/trusts-and-estates-trust-details-41g-trust",
        "SaTrustNotRegistered:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I don’t want to do this right now",
        "http://localhost:9020/business-account",
        "SaTrustNotRegistered:Click:NotNow"
      )

    }
  }
}
