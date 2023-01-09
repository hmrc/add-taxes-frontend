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
import controllers.ControllerSpecBase
import forms.sa.DoYouHaveSAUTRFormProvider
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.DoYouHaveSAUTR
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.internal.verification.VerificationModeFactory.times
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import utils.FakeNavigator
import views.html.sa.doYouHaveSAUTR

import scala.concurrent.Future

class DoYouHaveSAUTRControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad


  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  val formProvider = new DoYouHaveSAUTRFormProvider()
  val form: Form[DoYouHaveSAUTR] = formProvider()

  val view: doYouHaveSAUTR = injector.instanceOf[doYouHaveSAUTR]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]

  def controller(): DoYouHaveSAUTRController = {
    new DoYouHaveSAUTRController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      mockDataCacheConnector
    )
  }

  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
  }

  def viewAsString(form: Form[_] = form): String =
    new doYouHaveSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "DoYouHaveSAUTR Controller" must {

    when(mockDataCacheConnector.remove(any(), any())).thenReturn(Future.successful(true))

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveSAUTR.options.head.value)).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
      verify(mockDataCacheConnector, times(1)).remove(any(), any())
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
      verify(mockDataCacheConnector, times(1)).remove(any(), any())
    }

    for (option <- DoYouHaveSAUTR.options) {
      s"redirect to next page when '${option.value}' is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", option.value)).withMethod("POST")
        val result = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }
    }
  }
}
