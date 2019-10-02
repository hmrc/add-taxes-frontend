/*
 * Copyright 2019 HM Revenue & Customs
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
import org.mockito.Matchers
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatSubscriptionConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures with MockHttpClient {

  def vatSubscriptionConnector[A](
    mockedResponse: HttpResponse,
    httpWrapper: HttpWrapper = mock[HttpWrapper]): VatSubscriptionConnector = {
    when(httpWrapper.getF[A](Matchers.any())).thenReturn(mockedResponse)
    new VatSubscriptionConnector(http(httpWrapper), frontendAppConfig)
  }

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val vrn: String = "123456789"

  "VatConnector" when {

    "mandationStatus is called" should {

      "return Right(true) when the call is successful (200)" in {

        val response: Future[Either[String, Boolean]] =
          vatSubscriptionConnector(mockedResponse = HttpResponse(OK, None))
            .getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe Right(true)
        }
      }

      "return Right(false) if a VRN is not found in ETMP (404)" in {

        val response: Future[Either[String, Boolean]] =
          vatSubscriptionConnector(mockedResponse = HttpResponse(NOT_FOUND, None))
            .getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe Right(false)
        }
      }

      "return a Left('Failed')) if the response could not be mapped" in {

        val response: Future[Either[String, Boolean]] =
          vatSubscriptionConnector(mockedResponse = HttpResponse(INTERNAL_SERVER_ERROR, None))
            .getMandationStatus(vrn)

        whenReady(response) { result =>
          result mustBe Left("Failed")
        }
      }

    }

  }
}
