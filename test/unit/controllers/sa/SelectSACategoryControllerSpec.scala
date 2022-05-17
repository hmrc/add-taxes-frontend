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

package controllers.sa

import connectors.DataCacheConnector
import controllers._
import forms.sa.SelectSACategoryFormProvider
import models.sa.SelectSACategory
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.{CredFinderService, SelectSaCategoryService}
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

import scala.concurrent.Future

class SelectSACategoryControllerSpec extends ControllerSpecBase with MockitoSugar {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new SelectSACategoryFormProvider()
  val form: Form[SelectSACategory] = formProvider()
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockSaCategoryService: SelectSaCategoryService = mock[SelectSaCategoryService]
  val mockCredFinderService: CredFinderService = mock[CredFinderService]
  val view: selectSACategory = injector.instanceOf[selectSACategory]
  val btaOrigin: String = "bta-sa"

  val radioButtonOptions: Set[RadioOption] = SelectSACategory.options.filterNot(_.value == SelectSACategory.MtdIT.toString)

  def controller()(enrolmentTypes: HmrcEnrolmentType*): SelectSACategoryController = {
    new SelectSACategoryController(
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider,
      view,
      mockSaCategoryService,
      mockCredFinderService,
      mockDataCacheConnector,
      frontendAppConfig
    ) {override val accessMtdFeatureSwitch: Boolean = false}
  }

  def viewAsString(form: Form[_] = form, radioOptions: Set[RadioOption] = radioButtonOptions, origin: String): String =
    new selectSACategory(
      formWithCSRF, mainTemplate
    )(frontendAppConfig, form, routes.SelectSACategoryController.onSubmitHasUTR(origin), origin, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsStringNoUTR(form: Form[_] = form, radioOptions: Set[RadioOption] = radioButtonOptions, origin: String): String =
    new selectSACategory(
      formWithCSRF, mainTemplate
    )(frontendAppConfig, form, routes.SelectSACategoryController.onSubmitNoUTR(), origin, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  "SelectSACategory Controller" must {

    when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
      .thenReturn(Future.successful(Redirect(onwardRoute.url)))

    "return OK and the correct view for a GET" in {
      val result = controller()().onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(origin = btaOrigin)
    }

    "return OK and the correct view for a GET when called on the no UTR path" in {
      val result = controller()().onPageLoadNoUTR()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsStringNoUTR(origin = btaOrigin)
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", radioButtonOptions.head.value))
      val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm, origin = btaOrigin)
    }

    "return OK" in {
      val result = controller()().onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)

      status(result) mustBe OK
    }

    for (option <- radioButtonOptions) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value))
        val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }

    "show all options" when {
      val radioOptions: Set[RadioOption] = radioButtonOptions

      "on page load and not enrolled for SA or Trust" in {
        val result = controller()().onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions, origin = btaOrigin)

        contentAsString(result) mustBe view
      }

      "on page submit and not enrolled for SA or Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions, origin = btaOrigin)

        val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide 'Individual or sole trader' option" when {
      val radioOptions: Set[RadioOption] =
        radioButtonOptions.filterNot(_.value == SelectSACategory.Sa.toString)

      "on page load and enrolled for SA" in {
        val result = controller()(HmrcEnrolmentType.SA).onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions, origin = btaOrigin)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for SA" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions, btaOrigin)

        val result = controller()(HmrcEnrolmentType.SA).onSubmitHasUTR(btaOrigin)(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "hide 'Trust' option" when {
      val radioOptions: Set[RadioOption] =
        radioButtonOptions.filterNot(_.value == SelectSACategory.Trust.toString)

      "on page load and enrolled for Trust" in {
        val result = controller()(HmrcEnrolmentType.RegisterTrusts).onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)
        val view = viewAsString(radioOptions = radioOptions, origin = btaOrigin)

        contentAsString(result) mustBe view
      }

      "on page submit and enrolled for Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val view = viewAsString(boundForm, radioOptions, origin = btaOrigin)

        val result = controller()(HmrcEnrolmentType.RegisterTrusts).onSubmitHasUTR(btaOrigin)(postRequest)

        contentAsString(result) mustBe view
      }
    }

    "redirect to do you want to add a partner" when {
      "on page load and enrolled for SA and Trust" in {
        val result =
          controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")
      }

      "on submit and enrolled for SA and Trust" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val result = controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onSubmitHasUTR(btaOrigin)(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")
      }

      "on submit for SA and Trust on the no UTR path" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
        val result = controller()(HmrcEnrolmentType.SA, HmrcEnrolmentType.RegisterTrusts).onSubmitNoUTR()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")
      }
    }
  }
}
