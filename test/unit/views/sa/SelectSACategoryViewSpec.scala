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

package views.sa

import play.api.data.Form
import forms.sa.SelectSACategoryFormProvider
import models.sa.SelectSACategory
import play.api.mvc.Call
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.sa.selectSACategory

class SelectSACategoryViewSpec extends ViewBehaviours {

  lazy val onSubmit: Call = controllers.routes.IndexController.onPageLoad

  val messageKeyPrefix = "selectSACategory"

  val form = new SelectSACategoryFormProvider()()
  val btaOrigin: String = "bta-sa"
  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: (String) => HtmlFormat.Appendable = (origin: String) =>
    new selectSACategory(
      formWithCSRF, mainTemplate
    )(frontendAppConfig, form, onSubmit, origin, SelectSACategory.options)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: (Form[_], String) => HtmlFormat.Appendable = (form: Form[_], origin: String) =>
    new selectSACategory(
      formWithCSRF, mainTemplate
    )(frontendAppConfig, form, onSubmit, origin, SelectSACategory.options)(serviceInfoContent)(fakeRequest, messages)

  "SelectSACategory view" must {
    "contain heading ID" in {
      val doc = asDocument(createView(btaOrigin))
      doc.getElementsByTag("h1").attr("id") mustBe "select-sa-category"
    }
  }

  "SelectSACategory view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form, btaOrigin))
        for (option <- SelectSACategory.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- SelectSACategory.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}")), btaOrigin))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- SelectSACategory.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> "")), btaOrigin))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
}
