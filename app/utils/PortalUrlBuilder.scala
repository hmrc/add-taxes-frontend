/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import controllers.LanguageSwitchController.welsh
import play.api.mvc.Request
import uk.gov.hmrc.play.language.LanguageUtils

trait PortalUrlBuilder {

  val languageUtils: LanguageUtils

  def appendLanguage(url: String)(implicit request: Request[_]): String = {
    val lang = if (languageUtils.getCurrentLang == welsh) "lang=cym" else "lang=eng"
    val token = if (url.endsWith("?")) {
      ""
    } else if (url.contains("?")) {
      "&"
    } else {
      "?"
    }
    s"$url$token$lang"
  }

}
