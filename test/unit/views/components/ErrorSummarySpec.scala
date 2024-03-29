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
import play.api.data.FormError
import play.api.i18n.Messages
import play.twirl.api.Html
import views.html.components.error_summary

class ErrorSummarySpec extends SpecBase {

  implicit lazy val messagesImpl: Messages = messages

  val view: Html = error_summary(Seq(FormError("test", "test")))

  "error summary componant" must {

    "summary div must have role=alert and aria-labelledby" in {
      val doc: Document = Jsoup.parse(view.toString)

      val summaryDiv = doc.select("div.govuk-error-summary")

      summaryDiv.attr("role") mustBe "alert"
    }

    "include heading with heading text and error messages" in {
      val doc: Document = Jsoup.parse(view.toString)

      val heading = doc.select("h2")
      val errorList = doc.select("ul.govuk-error-summary__list > li")

      heading.text() mustBe "There is a problem"
      errorList.text() mustBe "test"
    }
  }
}
