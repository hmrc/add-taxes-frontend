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
import forms.employer.paye.WhatIsYourPAYEReferenceFormProvider
import models.employer.paye.PAYEReference
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.{EpayeEnrolmentChecker, FeatureToggleSupport}
import service.AuditService
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{Enrolments, FakeNavigator}
import views.html.employer.paye.whatIsYourPAYEReference

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhatIsYourPAYEReferenceControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  val formProvider = new WhatIsYourPAYEReferenceFormProvider()

  val form: Form[PAYEReference] = formProvider()

  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockAuditService: AuditService = mock[AuditService]

  val view: whatIsYourPAYEReference = injector.instanceOf[whatIsYourPAYEReference]

  def controller(empRefExists: Boolean, featureSwitch: Boolean = false): WhatIsYourPAYEReferenceController = {
    val desiredRoute = if(empRefExists) {
      Call("GET", frontendAppConfig.getBusinessAccountUrl("wrong-credentials"))
    } else {
      Call("GET", frontendAppConfig.emacEnrollmentsUrl(Enrolments.EPAYE))
    }
    new WhatIsYourPAYEReferenceController(
      frontendAppConfig,
      view,
      mcc,
      new FakeNavigator(desiredRoute = desiredRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      mockEnrolmentStoreProxyConnector,
      formProvider,
      mockAuditService
    ) {
      override val enrolmentCheckerFeature: Boolean = featureSwitch
    }
  }

  def viewAsString(): String =
    new whatIsYourPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsString(form: Form[PAYEReference] = form): String =
    new whatIsYourPAYEReference(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "WhatIsYourPAYEReferenceController Controller" must {

    when(mockAuditService.auditEPAYE(any(),any(),any())(any(),any(),any()))
      .thenReturn(Future.successful(AuditResult.Success))

    "return OK and the correct view for a GET" when {

      "epayeEnrolmentCheckerEnabled is enabled" in {
        enable(EpayeEnrolmentChecker)
        val result = controller(empRefExists = false, featureSwitch = true).onPageLoad()(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString()
      }

      "epayeEnrolmentCheckerEnabled is disabled" in {
        disable(EpayeEnrolmentChecker)
        val result = controller(empRefExists = false).onPageLoad()(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some("http://localhost:9555/enrolment-management-frontend/IR-PAYE/request-access-tax-scheme?continue=%2Fbusiness-account")
      }
    }
    "return bad request when invalid empRef is provided" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("empRef", "A1@S/D$^G*"))
      val boundForm = form.bind(Map("empRef"-> "A1@S/D$^G*"))

      val result = controller(empRefExists = false).onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect when valid empRef is submitted and are in the enrolment store" in {
      when(mockEnrolmentStoreProxyConnector.checkExistingEmpRef(any(), any())(any(), any())).thenReturn(Future.successful(true))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("empRef", "123/AB123"))

      val result = controller(empRefExists = true).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(Call("GET", frontendAppConfig.getBusinessAccountUrl("wrong-credentials")).url)
    }

    "redirect when valid empRef is submitted and are not in the enrolment store" in {
      when(mockEnrolmentStoreProxyConnector.checkExistingEmpRef(any(), any())(any(), any())).thenReturn(Future.successful(false))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("empRef", "123/AB123"))

      val result = controller(empRefExists = false).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(Call("GET", frontendAppConfig.emacEnrollmentsUrl(Enrolments.EPAYE)).url)
    }
  }

}
