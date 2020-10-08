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

import forms.sa.KnownFactsFormProvider
import models.sa.KnownFacts
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.KnownFactsFormValidator
import views.behaviours.ViewBehaviours
import views.html.sa.knownFacts

class KnownFactsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "knownFacts"
  val mockKnownFactsValidator: KnownFactsFormValidator = injector.instanceOf[KnownFactsFormValidator]


  val formProvider = new KnownFactsFormProvider(mockKnownFactsValidator, frontendAppConfig)
  val form = formProvider()
  val serviceInfoContent = HtmlFormat.empty

  def createView: knownFacts = injector.instanceOf[knownFacts]

  def createViewUsingForm:(Form[KnownFacts]) => HtmlFormat.Appendable = (form: Form[KnownFacts]) =>
    injector.instanceOf[knownFacts].apply(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "knownFacts view" must {
    //TODO Test when we have content for DL-4157
  }

}
