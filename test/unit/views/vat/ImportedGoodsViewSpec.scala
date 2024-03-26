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

import forms.vat.ImportedGoodsFormProvider
import models.vat.ImportedGoods
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Request
import play.twirl.api.{Html, HtmlFormat}
import service.ThresholdService
import views.behaviours.ViewBehaviours
import views.html.vat.importedGoods

class ImportedGoodsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "importedGoods"
  val thresholdService: ThresholdService = injector.instanceOf[ThresholdService]
  implicit val request: Request[_] = fakeRequest

  implicit val msg: Messages = messages

  val form = new ImportedGoodsFormProvider()(thresholdService.formattedVatThreshold())

  val serviceInfoContent: Html = HtmlFormat.empty

  def createView: () => HtmlFormat.Appendable = () =>
    new importedGoods(formWithCSRF, mainTemplate)(frontendAppConfig, form, thresholdService.formattedVatThreshold())(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    new importedGoods(formWithCSRF, mainTemplate)(frontendAppConfig, form, thresholdService.formattedVatThreshold())(serviceInfoContent)(fakeRequest, messages)

  "ImportedGoods view" must {
    behave like normalPageWithArg(createView, messageKeyPrefix, thresholdService.formattedVatThreshold())

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "imported-goods"
    }

    "include correct content" in {
      val doc = asDocument(createViewUsingForm(form))
      assertContainsText(
        doc,
        s"You need to register for VAT using form VAT1B if you have imported goods worth more than ${thresholdService.formattedVatThreshold()} from other EU member states.")
    }
  }

  "ImportedGoods view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- ImportedGoods.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- ImportedGoods.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- ImportedGoods.options.filterNot(_ == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title", thresholdService.formattedVatThreshold()))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
}
