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

package connectors

import base.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, NO_CONTENT, OK, PRECONDITION_FAILED}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreProxyConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val mockHttp: HttpClient = mock[HttpClient]

  def enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = {
    new EnrolmentStoreProxyConnector(frontendAppConfig, mockHttp)
  }

  val utr: String = "1234"
  val officeNumber: String = "123"
  val payeReference: String = "AB123"

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "EnrolmentStoreProxy" when {
    "checkExistingUtr is called" should {

      "return true when the call is successful (200)" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr)

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return false if no utr found (204)" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false if internal server error (500)" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.failed(new Exception))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr)

        whenReady(response) { result =>
          result mustBe false
        }
      }
    }

    "checkExistingEmpRef is called" should {

      "return true when the call is successful (200)" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return false if no officeNumber, payeReference found (204)" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false if internal server error (500)" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.failed(new Exception))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe false
        }
      }
    }

  }
}
