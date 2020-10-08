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

import play.api.i18n.{Lang, Messages}
import play.twirl.api.HtmlFormat
import views.ViewSpecBase
import views.html.components.language_selection

class LanguageSelectionViewSpec extends ViewSpecBase {

  val langMap = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def welshMessages: Messages = mcc.messagesApi.preferred(Seq(Lang("cy")))

  val view: () => HtmlFormat.Appendable = () => language_selection(langMap, frontendAppConfig.routeToSwitchLanguage)(messages)
  val welshView: () => HtmlFormat.Appendable = () => language_selection(langMap, frontendAppConfig.routeToSwitchLanguage)(welshMessages)

  "render a Cymraeg link if the language is set to English" in {
    val doc = asDocument(view())
    assertRenderedById(doc, "cymraeg-switch")
    val link = doc.getElementById("cymraeg-switch")
    assert(link.text() == "Cymraeg", "cymraeg-switch did not contain Cymraeg")
    assert(link.attr("href").endsWith("/language/cymraeg"), "cymraeg-switch end with a link to cymraeg")
    assert(
      link.attr("data-journey-click") == "Language:Click:Welsh",
      "cymraeg-switch did not have the right data-journey attribute")
  }

  "render an English link if the language is set to Cymraeg" in {
    val doc = asDocument(welshView())
    assertRenderedById(doc, "english-switch")
    val link = doc.getElementById("english-switch")
    assert(link.text() == "English", "english-switch did not contain Cymraeg")
    assert(link.attr("href").endsWith("/language/english"), "english-switch end with a link to english")
    assert(
      link.attr("data-journey-click") == "Language:Click:English",
      "english-switch did not have the right data-journey attribute")

  }

}
