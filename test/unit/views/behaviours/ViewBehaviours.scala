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

package views.behaviours

import play.api.i18n.Lang
import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  implicit val lang: Lang = Lang("en")

  val signOutAndRegisterAsOrgLink = "http://localhost:9553/bas-gateway/register?continueUrl=%2Fbusiness-account%2Fadd-tax&origin=unknown&accountType=organisation"

  def normalPage(view: () => HtmlFormat.Appendable, messageKeyPrefix: String, expectedGuidanceKeys: String*): Unit =
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view())
          val nav = doc.getElementsByClass("govuk-header__link govuk-header__service-name")
          nav.text mustBe "Business tax account"
        }

        "display the correct browser title" in {
          val doc = asDocument(view())

          assertEqualsMessage(doc, "title", "site.service_title", messages(s"$messageKeyPrefix.title"))
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view())
          assertRenderedByClass(doc, "hmrc-language-select__list")
        }

        "display the sign out link" in {
          val doc = asDocument(view())
          assertLinkByClass(
            doc,
            "govuk-link hmrc-sign-out-nav__link",
            "Sign out",
            "http://localhost:9020/business-account/sso-sign-out")
        }

        "contain the platform help links section with an link to the accessibility statement" in {
          val doc = asDocument(view())
          val linkSection = doc.getElementsByClass("govuk-footer__inline-list")

          linkSection.size() mustBe 1
          linkSection.select("li").size mustBe 7
          linkSection.select("li > a").get(1).attr("href") must include("/accessibility-statement/business-tax-account")
          linkSection.select("li > a").get(1).text mustBe "Accessibility statement"
        }
      }
    }

  def normalPageWithArg(view: () => HtmlFormat.Appendable, messageKeyPrefix: String, messageArg: String,expectedGuidanceKeys: String*): Unit =
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view())
          val nav = doc.getElementsByClass("govuk-header__link govuk-header__service-name")
          nav.text mustBe "Business tax account"
        }

        "display the correct browser title" in {
          val doc = asDocument(view())

          assertEqualsMessage(doc, "title", "site.service_title", messages(s"$messageKeyPrefix.title", messageArg))
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", messageArg)
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view())
          assertRenderedByClass(doc, "hmrc-language-select__list")
        }

        "display the sign out link" in {
          val doc = asDocument(view())
          assertLinkByClass(
            doc,
            "govuk-link hmrc-sign-out-nav__link",
            "Sign out",
            "http://localhost:9020/business-account/sso-sign-out")
        }

        "contain the platform help links section with an link to the accessibility statement" in {
          val doc = asDocument(view())
          val linkSection = doc.getElementsByClass("govuk-footer__inline-list")

          linkSection.size() mustBe 1
          linkSection.select("li").size mustBe 7
          linkSection.select("li > a").get(1).attr("href") must include("/accessibility-statement/business-tax-account")
          linkSection.select("li > a").get(1).text mustBe "Accessibility statement"
        }
      }
    }
  def pageWithBackLink(view: () => HtmlFormat.Appendable): Unit =
    "behave like a page with a back link" must {
      "have a back link" in {
        val doc = asDocument(view())
        assertRenderedById(doc, "back-link")
      }
    }
}



