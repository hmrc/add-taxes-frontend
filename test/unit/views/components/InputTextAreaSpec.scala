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
import views.html.components.input_textarea

class InputTextAreaSpec extends SpecBase {

  val id = "testId"

  val testForm = Form(
    "value" -> boolean
  )

  def inputTextArea(field: Field): Html = input_textarea(
    field,
    "testLabel"
  )

  val testField: Field = testForm("value")
  implicit val messagesImpl: Messages = messages


  "inputYesNo must" must {
    "not include error markups when form doesnot have errors" in {
      val doc: Document = Jsoup.parse(inputTextArea(testField).toString)

      val forms = doc.select("div.govuk-form-group")
      forms.size mustBe 1

      forms.get(0).className() mustBe "govuk-form-group"
    }

    "include error markups when there is an form error" in {
      val erroredField = testField.copy(errors = Seq(FormError("testErrorKey", "testErrorMessage")))

      val doc: Document = Jsoup.parse(inputTextArea(erroredField).toString)

      val forms = doc.select("div.govuk-form-group")
      forms.size mustBe 1

      forms.get(0).className().split(" ").filter(_.nonEmpty) mustBe Array("govuk-form-group", "form-group-error", "govuk-form-group--error")

      doc.getElementById("error-message-value-input").hasClass("error-message")
      doc.getElementById("visually-hidden-error-prefix").text() mustBe "Error:"
      doc.getElementById("visually-hidden-error-prefix").hasClass("govuk-visually-hidden")
    }
  }
}
