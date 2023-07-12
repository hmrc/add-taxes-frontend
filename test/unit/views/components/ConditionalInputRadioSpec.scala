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

package views.components

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.twirl.api.Html
import utils.RadioOption
import views.html.components.conditional_input_radio

class ConditionalInputRadioSpec extends SpecBase {

  val testLegend = ""

  val id = "testId"

  val testRadios: Seq[RadioOption] =
  Seq(RadioOption("conditionalInputRadio1", "Yes"), RadioOption("conditionalInputRadio2", "No"))
  val testForm = Form(
    "value" -> boolean
  )
  implicit val messagesImpl: Messages = messages


  def conditionalInputRadio(field: Field, hint: Option[String] = None,
                            conditionalHint: Option[String] = None,
                            radioOptions: Seq[RadioOption] = Seq(RadioOption("conditionalInputRadio1", "Yes"))): Html = conditional_input_radio(
    field,
    legendText = Some(testLegend),
    inputs = radioOptions,
    hint = hint,
    conditionalHint = conditionalHint
  )

  val testField: Field = testForm("value")

  "inputRadio" must {
    "not include error markups when form does not have errors" in {
      val doc: Document = Jsoup.parse(conditionalInputRadio(testField).toString)

      val forms = doc.select("div.govuk-form-group")
      forms.size mustBe 1

      forms.get(0).className().split(" ").filter(_.nonEmpty) mustBe Array("govuk-form-group")
    }

    "include hint text as aria-describedby for fieldset when hint is present" in {
      val doc: Document = Jsoup.parse(conditionalInputRadio(testField, Some("hint")).toString)

      val forms = doc.select("fieldset")
      forms.size mustBe 1

      forms.get(0).attr("aria-describedby") mustBe "form-hint-text"
    }

    "include no hint text as aria-describedby for fieldset when no hint is present" in {
      val doc: Document = Jsoup.parse(conditionalInputRadio(testField, None).toString)

      val forms = doc.select("fieldset")
      forms.size mustBe 1

      forms.get(0).attr("aria-describedby") mustBe ""
    }

    "include no conditional hidden text for Yes radio option" in {
      val doc: Document = Jsoup.parse(conditionalInputRadio(testField, None).toString)

      val forms = doc.select("fieldset")
      forms.size mustBe 1

      doc.getElementsByClass("govuk-label").attr("option-true") mustBe ""
    }

    "include no conditional hidden text for No radio option" in {
      val testConditionalHint = "test conditional hint"
      val testNo = testField.copy(value = Some("option-false"))
      val doc: Document = Jsoup.parse(conditionalInputRadio(testField, None, Some(testConditionalHint), Seq(RadioOption("conditionalInputRadio1", "No"))).toString)

      val forms = doc.select("fieldset")
      forms.size mustBe 1


      doc.getElementById("conditionalHintText").text() must include(testConditionalHint)

    }

    "include error markups when there is an form error" in {
      val erroredField = testField.copy(errors = Seq(FormError("testErrorKey", "testErrorMessage")))

      val doc: Document = Jsoup.parse(conditionalInputRadio(erroredField).toString)

      val forms = doc.select("div.govuk-form-group")
      forms.size mustBe 1

      forms.get(0).className()  mustBe "govuk-form-group form-group-error govuk-form-group--error"

      doc.getElementById("error-message-value-input").hasClass("error-message")
      doc.getElementById("visually-hidden-error-prefix").text() mustBe "Error:"
      doc.getElementById("visually-hidden-error-prefix").hasClass("govuk-visually-hidden")
    }
  }
}
