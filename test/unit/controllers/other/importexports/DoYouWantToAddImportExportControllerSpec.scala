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

package controllers.other.importexports

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.{ARSContentSwitch, AtarSwitch}
import config.featureToggles.FeatureToggleSupport.isEnabled
import controllers.ControllerSpecBase
import forms.other.importexports.DoYouWantToAddImportExportFormProvider
import models.other.importexports.DoYouWantToAddImportExport
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.AuditService
import utils.FakeNavigator
import views.html.other.importexports.doYouWantToAddImportExport

import scala.concurrent.ExecutionContext.Implicits.global

class DoYouWantToAddImportExportControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new DoYouWantToAddImportExportFormProvider()
  val form: Form[DoYouWantToAddImportExport] = formProvider()
  implicit val mockConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  val view: doYouWantToAddImportExport = injector.instanceOf[doYouWantToAddImportExport]
  val mockAuditService: AuditService = mock[AuditService]

  def controller() =
    new DoYouWantToAddImportExportController(
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      mockAuditService
    )

  def viewAsString(form: Form[_] = form): String =
    new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(frontendAppConfig, form, isEnabled(AtarSwitch), isEnabled(ARSContentSwitch))(HtmlFormat.empty)(fakeRequest, messages).toString

  "DoYouWantToAddImportExport Controller" must {

    "return OK and the correct view for a GET" in {
      disable(ARSContentSwitch)
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val atarBool = isEnabled(AtarSwitch)
      disable(ARSContentSwitch)

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouWantToAddImportExport.options(atarBool, arsAddTaxSwitch = false).head.value)).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid value"))
      disable(ARSContentSwitch)

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))
      disable(ARSContentSwitch)

      status(result) mustBe OK
    }

    val atarBool = isEnabled(AtarSwitch)
    for (option <- DoYouWantToAddImportExport.options(atarBool, arsAddTaxSwitch = false)) {
      disable(ARSContentSwitch)
      s"redirect to next page when '${option.value}' is submitted" in {
        disable(ARSContentSwitch)
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }


    for (option <- DoYouWantToAddImportExport.options(atarBool, arsAddTaxSwitch = true)) {
      enable(ARSContentSwitch)
      s"redirect to next page when ars switch is set to true '${option.value}' is submitted" in {
        enable(ARSContentSwitch)
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
        }
      }
    }
  }
