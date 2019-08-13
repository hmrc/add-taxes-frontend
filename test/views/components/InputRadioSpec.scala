/*
 * Copyright 2019 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.{Field, Form}
import play.api.i18n.{Messages, MessagesApi}
import play.twirl.api.{Html, HtmlFormat}
import utils.RadioOption
import views.html.components.input_radio
import play.api.data._
import play.api.data.Forms._

class InputRadioSpec extends WordSpec with MustMatchers with GuiceOneAppPerSuite {

  implicit lazy val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq.empty)

  val testLegend = ""

  val id = "testId"

  val testRadios: Seq[RadioOption] =
    Seq(RadioOption("option-true", "true", ""), RadioOption("option-false", "false", ""))

  val testForm = Form(
    "value" -> boolean
  )

  def inputRadio(field: Field): Html = input_radio(
    field,
    testLegend,
    testRadios
  )

  val testField: Field = testForm("value")

  "inputRadio" must {
    "not include error markups when form doenot have errors" in {
      val doc: Document = Jsoup.parse(inputRadio(testField).toString)

      val forms = doc.select("div.form-group")
      forms.size mustBe 1

      forms.get(0).className().split(" ").filter(_.nonEmpty) mustBe Array("form-group", "margin-top-medium")
    }
    "include error markups when there is an form error" in {
      val erroredField = testField.copy(errors = Seq(FormError("testErrorKey", "testErrorMessage")))

      val doc: Document = Jsoup.parse(inputRadio(erroredField).toString)

      val forms = doc.select("div.form-group")
      forms.size mustBe 1

      forms.get(0).className().split(" ").filter(_.nonEmpty) mustBe Array(
        "form-group",
        "form-group-error",
        "margin-top-medium")

      doc.getElementById("error-message-value-input").hasClass("error-message")
    }
  }

}
