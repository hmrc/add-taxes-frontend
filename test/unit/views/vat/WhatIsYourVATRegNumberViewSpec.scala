/*
 * Copyright 2025 HM Revenue & Customs
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

import forms.vat.WhatIsYourVATRegNumberFormProvider
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.vat.whatIsYourVATRegNumber

class WhatIsYourVATRegNumberViewSpec extends ViewBehaviours {

  private val isKnownFactsCheckEnabled = true

  val messageKeyPrefix = "whatIsYourVATRegNumber"

  val form = new WhatIsYourVATRegNumberFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  val whatIsYourVATRegNumberPage: whatIsYourVATRegNumber = app.injector.instanceOf[whatIsYourVATRegNumber]

  def createView(isKnownFactsCheckEnabled: Boolean): () => Html =
    () => whatIsYourVATRegNumberPage(frontendAppConfig, form, isKnownFactsCheckEnabled)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm(isKnownFactsCheckEnabled: Boolean): Form[String] => HtmlFormat.Appendable = (form: Form[String]) =>
    whatIsYourVATRegNumberPage(frontendAppConfig, form, isKnownFactsCheckEnabled)(serviceInfoContent)(fakeRequest, messages)

  def viewIncludes(s: String): Unit = asDocument(createView(isKnownFactsCheckEnabled)()).text() must include(s)

  private val renderedDoc = asDocument(createView(isKnownFactsCheckEnabled)())
  private val errorView   = asDocument(createViewUsingForm(isKnownFactsCheckEnabled)(form.bind(Map("value" -> ""))))

  "WhatIsYourVATRegNumber view" must {
    "have the correct banner title" in {
      val nav = renderedDoc.getElementsByClass("govuk-header__link govuk-header__service-name")
      nav.text mustBe "Business tax account"
    }
    "display the correct browser title" in {
      assertEqualsMessage(renderedDoc, "title", "site.service_title", messages(s"$messageKeyPrefix.title.new"))
    }
    "display the correct page header" in {
      assertPageTitleEqualsMessage(renderedDoc, s"$messageKeyPrefix.title.new")
    }
    "contain heading id" in {
      renderedDoc.getElementsByTag("h1").attr("id") mustBe "enter-vat-registration-heading"
    }

    "display hint text" in {
      viewIncludes("This is the 9-digit number on your VAT registration certificate. For example, 123456789.")
    }
    "contain a text box for the value" in {
      assertRenderedByCssSelector(renderedDoc, ".form-control-wrapper")
    }
    "include label for textField - What is your VAT registration number?" in {
      assertContainsLabel(renderedDoc, "value", "What is your VAT registration number?")
    }

    "display language toggles" in {
      assertRenderedByClass(renderedDoc, "hmrc-language-select__list")
    }
    "display the sign out link" in {
      assertLinkByClass(renderedDoc, "govuk-link hmrc-sign-out-nav__link", "Sign out", "http://localhost:9020/business-account/sso-sign-out")
    }

    "when invalid data is sent" must {
      "prepend title with Error: " in {
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title.new"))
        assertEqualsMessage(errorView, "title", "error.browser.title", title)
      }
      "show error summary when there is error " in {
        assertRenderedById(errorView, "error-summary-heading")
      }
    }
  }

}
