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

import connectors.EnrolmentStoreProxyConnector
import controllers.ControllerSpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class EnrolForSaControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockHttp: HttpClient = mock[HttpClient]

  val controller = new EnrolForSaController(
      mockEnrolmentStoreProxyConnector,
      FakeAuthAction,
      FakeServiceInfoAction,
      mcc,
      frontendAppConfig
    )

  val utr = "utr"
  implicit val ec: ExecutionContext = mcc.executionContext

  "Enrol Controller" must {
    "return 303 and correct url when enrol returns CREATED" in {
      when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(CREATED, "")))

      val result = controller.enrolForSa(utr, groupId, providerId)(ec)(fakeRequest)

      status(result) mustBe 303
      redirectLocation(result).toString must include("/business-account")
    }

    "return 303 and correct url when enrol returns BAD_REQUEST" in {
      when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))

      val result = controller.enrolForSa(utr, groupId, providerId)(ec)(fakeRequest)

      status(result) mustBe 303
      redirectLocation(result).toString must include("/business-account/wrong-credentials")
    }

    "return 303 and correct url when enrol returns NO_CONTENT" in {
      when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(NO_CONTENT, "")))

      val result = controller.enrolForSa(utr, groupId, providerId)(ec)(fakeRequest)

      status(result) mustBe 303
      redirectLocation(result).toString must include("/business-account/wrong-credentials")
    }

    "return 303 and correct url when enrol returns FORBIDDEN" in {
      when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(FORBIDDEN, "")))

      val result = controller.enrolForSa(utr, groupId, providerId)(ec)(fakeRequest)

      status(result) mustBe 303
      redirectLocation(result).toString must include("/business-account/wrong-credentials")
    }

    "return 303 and correct url when enrol returns INTERNAL SERVER ERROR" in {
      when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

      val result = controller.enrolForSa(utr, groupId, providerId)(ec)(fakeRequest)

      status(result) mustBe 303
      redirectLocation(result).toString must include("/business-account/wrong-credentials")
    }
  }
}
