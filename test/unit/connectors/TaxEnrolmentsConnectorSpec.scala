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
import models.sa._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaxEnrolmentsConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val mockHttp: HttpClient = mock[HttpClient]

  def taxEnrolmentsConnector: TaxEnrolmentsConnector = {
    new TaxEnrolmentsConnector(frontendAppConfig, mockHttp)
  }

  val utr: String = "1234"
  val officeNumber: String = "123"
  val payeReference: String = "AB123"
  val groupId: String = "1EB60093-674F-4D03-A0FF-916E0D337319"

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "TaxEnrolmentsConnector" when {

    "enrolForSa is called" should {
      val userId: String = "1212121212121"
      val utr: String = "1234"
      val groupId: String = "12121212"
      val enrolActivate: String = "enrolAndActivate"

      "return created when the call is successful (201)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(CREATED, "")))
        val response = taxEnrolmentsConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return no content if no utr found (204)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(NO_CONTENT, "")))
        val response = taxEnrolmentsConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return forbidden  if no utr found (403)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(FORBIDDEN, "")))
        val response = taxEnrolmentsConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return bad request  if no utr found (400)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))
        val response = taxEnrolmentsConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return internal server error if internal server error (500)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

        val response = taxEnrolmentsConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }
    }
  }
}
