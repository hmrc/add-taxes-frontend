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

package controllers.vat

import config.featureToggles.FeatureSwitch.VatOssSwitch
import config.featureToggles.FeatureToggleSupport
import connectors.OssConnector
import controllers.ControllerSpecBase
import forms.vat.WhichVATServicesToAddFormProvider
import handlers.ErrorHandler
import models.vat.WhichVATServicesToAdd.VATOSS
import models.vat.{OssRecievedDetails, WhichVATServicesToAdd}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{FakeNavigator, HmrcEnrolmentType, RadioOption}
import views.html.vat.whichVATServicesToAdd

import scala.concurrent.Future

class WhichVATServicesToAddControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new WhichVATServicesToAddFormProvider()
  val form: Form[WhichVATServicesToAdd] = formProvider()
  val mockOssConnector: OssConnector = mock[OssConnector]
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  val view: whichVATServicesToAdd = injector.instanceOf[whichVATServicesToAdd]

  def controller()(enrolments: HmrcEnrolmentType*): WhichVATServicesToAddController = {
    new WhichVATServicesToAddController(
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction(enrolments: _*),
      formProvider,
      mockOssConnector,
      errorHandler,
      view,
      frontendAppConfig
    )
  }

  def viewAsString(form: Form[_] = form, radioOptions: Seq[RadioOption] = WhichVATServicesToAdd.options()): String =
    new whichVATServicesToAdd(formWithCSRF, mainTemplate)(frontendAppConfig, form, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  "WhichVATServicesToAdd Controller" must {
    "return OK and the correct view for a GET" in {
      disable(VatOssSwitch)
      val result = controller()().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      disable(VatOssSwitch)
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", WhichVATServicesToAdd.options().head.value)).withMethod("POST")

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      disable(VatOssSwitch)
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "return OK" in {
      disable(VatOssSwitch)
      val result = controller()().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
    }

    for (option <- WhichVATServicesToAdd.options()) {
      s"redirect to next page when '${option.value}' is submitted" in {
        disable(VatOssSwitch)
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller()().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "return OK and the correct view for a GET when switch is enabled" in {
      enable(VatOssSwitch)
      val result = controller()().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(radioOptions = WhichVATServicesToAdd.options(ossFeatureSwitch = true))
    }

    "redirect to the next page when valid data is submitted when switch is enabled" in {
      enable(VatOssSwitch)
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", WhichVATServicesToAdd.options(ossFeatureSwitch = true).head.value)).withMethod("POST")

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted when switch is enabled" in {
      enable(VatOssSwitch)
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm, radioOptions = WhichVATServicesToAdd.options(ossFeatureSwitch = true))
    }

    "return OK when switch is enabled" in {
      enable(VatOssSwitch)
      val result = controller()().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
    }

    for (option <- WhichVATServicesToAdd.options(ossFeatureSwitch = true)) {
      s"redirect to next page when '${option.value}' is submitted when switch is enabled" in {
        enable(VatOssSwitch)
        when(mockOssConnector.ossRegistrationJourneyLink()(any(), any()))
          .thenReturn(Future.successful(OssRecievedDetails(Some(" /test-url"))))
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller()().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
      if(option.value == VATOSS.toString) {
        s"internal error if the oss call fails and returns None switch is enabled" in {
          enable(VatOssSwitch)
          when(mockOssConnector.ossRegistrationJourneyLink()(any(), any()))
            .thenReturn(Future.successful(OssRecievedDetails(None)))
          val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
          val result = controller()().onSubmit()(postRequest)

          status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "not display vat radio option" when {

      "page is loaded and vat is enrolled" in {
        val radioOptions = WhichVATServicesToAdd.options().filterNot(_.value == "vat")
        disable(VatOssSwitch)
        val result = controller()(HmrcEnrolmentType.VAT).onPageLoad()(fakeRequest.withMethod("GET"))

        contentAsString(result) mustBe viewAsString(radioOptions = radioOptions)
      }

      "page is loaded and MTDVAT is enrolled" in {
        disable(VatOssSwitch)
        val radioOptions = WhichVATServicesToAdd.options().filterNot(_.value == "vatoss")

        val result = controller()(HmrcEnrolmentType.MTDVAT).onPageLoad()(fakeRequest.withMethod("GET"))

        contentAsString(result) mustBe viewAsString(radioOptions = radioOptions)
      }

      "page is loaded and OSS is enrolled" in {
        disable(VatOssSwitch)
        val radioOptions = WhichVATServicesToAdd.options().filterNot(_.value == "vatoss")

        val result = controller()(HmrcEnrolmentType.OSS).onPageLoad()(fakeRequest.withMethod("GET"))

        contentAsString(result) mustBe viewAsString(radioOptions = radioOptions)
      }

      "page errors and vat is enrolled " in {
        disable(VatOssSwitch)
        val radioOptions = WhichVATServicesToAdd.options().filterNot(_.value == "vat")
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = controller()(HmrcEnrolmentType.VAT).onSubmit()(postRequest)

        contentAsString(result) mustBe viewAsString(boundForm, radioOptions)
      }

      "page is loaded and vat is enrolled and switch is enabled" in {
        enable(VatOssSwitch)
        val result = controller()(HmrcEnrolmentType.VAT).onPageLoad()(fakeRequest.withMethod("GET"))

        contentAsString(result) mustBe viewAsString(
          radioOptions = WhichVATServicesToAdd.options(ossFeatureSwitch = true).filterNot(_.value == "vat"))
      }

      "page is loaded and MTDVAT is enrolled and switch is enabled" in {
        enable(VatOssSwitch)
        val result = controller()(HmrcEnrolmentType.MTDVAT).onPageLoad()(fakeRequest.withMethod("GET"))

        contentAsString(result) mustBe viewAsString(
          radioOptions = WhichVATServicesToAdd.options(ossFeatureSwitch = true))
      }

      "page is loaded and OSS is enrolled and switch is enabled" in {
        enable(VatOssSwitch)
        val result = controller()(HmrcEnrolmentType.OSS).onPageLoad()(fakeRequest.withMethod("GET"))

        contentAsString(result) mustBe viewAsString(
          radioOptions = WhichVATServicesToAdd.options(ossFeatureSwitch = true).filterNot(_.value == "vatoss"))
      }

      "page errors and vat is enrolled and switch is enabled" in {
        enable(VatOssSwitch)
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = controller()(HmrcEnrolmentType.VAT).onSubmit()(postRequest)

        contentAsString(result) mustBe viewAsString(boundForm,
          radioOptions = WhichVATServicesToAdd.options(ossFeatureSwitch = true).filterNot(_.value == "vat"))
      }
    }
  }
}
