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
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK, PRECONDITION_FAILED}
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatSubscriptionConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val mockHttp: HttpClient = mock[HttpClient]

  def vatSubscriptionConnector: VatSubscriptionConnector = {
    new VatSubscriptionConnector(mockHttp, frontendAppConfig)
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  val vrn: String = "123456789"

  "VatConnector" when {
    "mandationStatus is called" should {

      "return Right(true) when the call is successful (200)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(),any(),any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, "OK")))

        val response: Future[Int] = vatSubscriptionConnector.getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe OK
        }
      }

      "return Right(false) if a VRN is not found in ETMP (404)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(),any(),any()))
          .thenReturn(Future.successful(HttpResponse.apply(NOT_FOUND, "NOT_FOUND")))

        val response: Future[Int] = vatSubscriptionConnector.getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe NOT_FOUND
        }

      }

      "return Right(false) if a VRN is not found in ETMP (UpstreamErrorResponse(404))" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(),any(),any()))
          .thenReturn(Future.failed(UpstreamErrorResponse("Not found", NOT_FOUND, NOT_FOUND)))

        val response: Future[Int] = vatSubscriptionConnector.getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe NOT_FOUND
        }

      }

      "return Right(false) if a VRN is pre-condition failed in ETMP (UpstreamErrorResponse(412))" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(),any(),any()))
          .thenReturn(Future.failed(UpstreamErrorResponse("Pre-condition failed", PRECONDITION_FAILED, PRECONDITION_FAILED)))

        val response: Future[Int] = vatSubscriptionConnector.getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe PRECONDITION_FAILED
        }

      }

      "return Left(true) if a VRN is in migration (412)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(),any(),any()))
          .thenReturn(Future.successful(HttpResponse.apply(PRECONDITION_FAILED, "NOT_FOUND")))

        val response: Future[Int] = vatSubscriptionConnector.getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe PRECONDITION_FAILED
        }
      }

      "return a Left(false) if the response could not be mapped" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(),any(),any()))
          .thenReturn(Future.successful(HttpResponse.apply(BAD_REQUEST, "BAD_REQUEST")))

        val response: Future[Int] = vatSubscriptionConnector.getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe BAD_REQUEST
        }
      }

    }

  }
}
