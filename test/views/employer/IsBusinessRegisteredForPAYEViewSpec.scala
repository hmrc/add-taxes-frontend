/*
 * Copyright 2018 HM Revenue & Customs
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

package views.employer

import controllers.employer.intermediaries.routes
import forms.employer.IsBusinessRegisteredForPAYEFormProvider
import models.employer.IsBusinessRegisteredForPAYE
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.ViewAction
import views.behaviours.ViewBehaviours
import views.html.employer.isBusinessRegisteredForPAYE

class IsBusinessRegisteredForPAYEViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "isBusinessRegisteredForPAYE"

  val form = new IsBusinessRegisteredForPAYEFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  val viewAction = ViewAction(controllers.routes.IndexController.onPageLoad(), "")

  def createView =
    () => isBusinessRegisteredForPAYE(frontendAppConfig, form, viewAction)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) =>
      isBusinessRegisteredForPAYE(frontendAppConfig, form, viewAction)(serviceInfoContent)(fakeRequest, messages)
  (form: Form[_]) =>
    isBusinessRegisteredForPAYE(
      frontendAppConfig,
      form,
      ViewAction(routes.IsBusinessRegisteredForPAYEController.onSubmit(), "AddIntermediariesEpayeOnline"))(
      serviceInfoContent)(fakeRequest, messages)

  "IsBusinessRegisteredForPAYE view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "IsBusinessRegisteredForPAYE view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- IsBusinessRegisteredForPAYE.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- IsBusinessRegisteredForPAYE.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- IsBusinessRegisteredForPAYE.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))

        assertEqualsMessage(doc, "title", "error.browser.title", messages(s"$messageKeyPrefix.title"))
      }
    }
  }
}
