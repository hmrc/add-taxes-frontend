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

package views

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.organisation_only

import scala.jdk.CollectionConverters._

class OrganisationOnlyViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "organisationOnly"

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new organisation_only(formWithCSRF, mainTemplate)(frontendAppConfig)(serviceInfoContent)(fakeRequest, messages)

  "OrganisationOnly view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "org-only"
    }
  }

  "OrganisationOnly view" when {
    "rendered" must {
      "contain the correct content" in {
        val view = asDocument(createView()).text()
        val doc = asDocument(createView())

        view must include("Set up a new account")
        view must include("You can’t add business taxes to this account.")
        view must include("You need to:")
        view must include("Don’t try to add business taxes on the Government Gateway website.")

        val expectedText = List(
          "Create an Organisation account on the Government Gateway website.",
          "Sign in to HMRC with the new sign in details.",
          "Add business taxes in that account."
        )

        val bulletList = doc.getElementsByClass("govuk-list--number").first().getElementsByTag("li").asScala.toList

        bulletList.zip(expectedText).foreach {
          case (element, string) =>
            element.text() mustBe string
        }
      }
      "have the correct links" in {
        val doc = asDocument(createView())
        assertLinkById(
          doc = doc,
          linkId = "create-account",
          expectedText = "Sign out and create ‘Organisation’ account",
          expectedUrl = signOutAndRegisterAsOrgLink,
          expectedRole = Some("button")
        )

        assertLinkById(
          doc,
          "not-now",
          "I do not want to do this now",
          "http://localhost:9020/business-account")
      }
    }
  }
}
