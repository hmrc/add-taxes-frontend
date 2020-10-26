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

package service

import connectors.EnrolmentStoreProxyConnector
import controllers.Assets._
import controllers.ControllerSpecBase
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolForSaServiceSpec extends ControllerSpecBase with MockitoSugar {

  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
    HtmlFormat.empty)


  def service = new EnrolForSaService(
    mockEnrolmentStoreProxyConnector
  )

  val utr: String = "utr"
  val credId: String = "cred"

  "EnrolForSaService" when {
    "enrolForSa is called" must {
      "returns false when Enrolment connector returns an exception" in {
        when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
          .thenReturn(Future.failed(new Exception("")))
        val result = service.enrolForSa(utr, credId, groupId)

        whenReady(result) { result =>
          result mustBe false
        }
      }
      "returns false when Enrolment connector returns NOT_FOUND" in {
        when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(NOT_FOUND, "")))
        val result = service.enrolForSa(utr, credId, groupId)

        whenReady(result) { result =>
          result mustBe false
        }
      }

      "returns false when Enrolment connector returns FORBIDDEN" in {
        when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(FORBIDDEN, "")))
        val result = service.enrolForSa(utr, credId, groupId)

        whenReady(result) { result =>
          result mustBe false
        }
      }

      "returns true when Enrolment connector returns CREATED" in {
        when(mockEnrolmentStoreProxyConnector.enrolForSa(any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(CREATED, "")))

        val result = service.enrolForSa(utr, credId, groupId)

        whenReady(result) { result =>
          result mustBe true
        }
      }
     }
   }
}
