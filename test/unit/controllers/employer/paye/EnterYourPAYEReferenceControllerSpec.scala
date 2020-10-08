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

package controllers.employer.paye

import connectors.EnrolmentStoreProxyConnector
import controllers._
import forms.employer.paye.PAYEReferenceFormProvider
import models.employer.paye.PAYEReference
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.{EpayeEnrolmentChecker, FeatureToggleSupport}
import utils.{Enrolments, FakeNavigator}
import views.html.employer.paye.enterPAYEReference

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnterYourPAYEReferenceControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  val formProvider = new PAYEReferenceFormProvider()

  val form: Form[PAYEReference] = formProvider()

  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]

  val view: enterPAYEReference = injector.instanceOf[enterPAYEReference]

  def controller(empRefExists: Boolean, featureSwitch: Boolean = false): EnterYourPAYEReferenceController = {
    val desiredRoute = if(empRefExists) {
      Call("GET", frontendAppConfig.getBusinessAccountUrl("wrong-credentials"))
    } else {
      Call("GET", frontendAppConfig.emacEnrollmentsUrl(Enrolments.EPAYE))
    }
    new EnterYourPAYEReferenceController(
      frontendAppConfig,
      view,
      mcc,
      new FakeNavigator(desiredRoute = desiredRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      mockEnrolmentStoreProxyConnector,
      formProvider
    ) {
      override val enrolmentCheckerFeature = featureSwitch
    }
  }

  def viewAsString(): String =
    new enterPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsString(form: Form[PAYEReference] = form): String =
    new enterPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "EnterYourPAYEReferenceController Controller" must {

    "return OK and the correct view for a GET" when {

      "epayeEnrolmentCheckerEnabled is enabled" in {
        val result = controller(false, true).onPageLoad()(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString()
      }

      "epayeEnrolmentCheckerEnabled is disabled" in {
        val result = controller(false).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("http://localhost:9555/enrolment-management-frontend/IR-PAYE/request-access-tax-scheme?continue=%2Fbusiness-account")
      }

    }

    "return bad request when invalid officeNumber and payeReference are provided" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("officeNumber", "ads"),("payeReference", ":_£("))
      val boundForm = form.bind(Map("officeNumber" -> "ads", "payeReference" -> ":_£("))

      val result = controller(false).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect when valid officeNumber and payeReference are submitted and are in the enrolment store" in {
      when(mockEnrolmentStoreProxyConnector.checkExistingEmpRef(any(), any())(any(), any())).thenReturn(Future.successful(true))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("officeNumber", "123"),("payeReference", "AB123"))

      val result = controller(true).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(Call("GET", frontendAppConfig.getBusinessAccountUrl("wrong-credentials")).url)
    }

    "redirect when valid officeNumber and payeReference is submitted and are not in the enrolment store" in {
      when(mockEnrolmentStoreProxyConnector.checkExistingEmpRef(any(), any())(any(), any())).thenReturn(Future.successful(false))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("officeNumber", "123"),("payeReference", "AB123"))

      val result = controller(false).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(Call("GET", frontendAppConfig.emacEnrollmentsUrl(Enrolments.EPAYE)).url)
    }
  }

}
