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
import models.sa.{KnownFacts, KnownFactsAndIdentifiers, KnownFactsReturn, SAUTR, SaEnrolment}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
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
  val groupId: String = "1EB60093-674F-4D03-A0FF-916E0D337319"

  val es0ResponsePrincipals: JsValue = Json.parse(
    """
      |{
      |    "principalUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ],
      |    "delegatedUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ]
      |}
      |""".stripMargin
  )

  val es0ResponseNoPrincipal: JsValue = Json.parse(
    """
      |{
      |    "delegatedUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ]
      |}
      |""".stripMargin
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "EnrolmentStoreProxy" when {
    "checkExistingUtr is called" should {

      "return true when ES0 returns some principal enrolments" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, es0ResponsePrincipals, Map("" -> Seq.empty))))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr)

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return false when ES0 returns only delegated enrolments" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, es0ResponseNoPrincipal, Map("" -> Seq.empty))))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr)

        whenReady(response) { result =>
          result mustBe false
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

    "checkSaGroup is called" should {
      "return true when OK is returned from ES3" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId)
        whenReady(response) { res =>
          res mustBe true
        }
      }

      "return false when NO_CONTENT is returned from ES3" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, "")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId)
        whenReady(response) { res =>
          res mustBe false
        }
      }

      "return false when INTERNAL_SERVER_ERROR is returned from ES3" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId)
        whenReady(response) { res =>
          res mustBe false
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.GET[HttpResponse](any())(any(), any(), any()))
          .thenReturn(Future.failed(new Exception))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId)

        whenReady(response) { result =>
          result mustBe false
        }
      }
    }

    "queryKnownFacts is called" should {
      val knownFacts = new KnownFacts(Some("AB112233D"), Some("SW1A 2AA"), None)
      val saUtr = new SAUTR("AB112233D")
      val responseKnownFacts = KnownFactsAndIdentifiers("IR-SA", Some("AB112233D"), Some("SW1A 2AA"), None)
      val responseJson = Json.toJson(responseKnownFacts).toString()

      "return true when the call is successful (200)" in {
        when(mockHttp.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, responseJson)))
        val response: Future[KnownFactsReturn] = enrolmentStoreProxyConnector.queryKnownFacts(saUtr, knownFacts)

        whenReady(response) { result =>
          result mustBe KnownFactsReturn("AB112233D", knownFactsResult = true)
        }
      }

      "return false if no utr found (204)" in {
        when(mockHttp.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, responseJson)))

        val response: Future[KnownFactsReturn] = enrolmentStoreProxyConnector.queryKnownFacts(saUtr, knownFacts)

        whenReady(response) { result =>
          result mustBe KnownFactsReturn("AB112233D", knownFactsResult = false)
        }
      }

      "return false if internal server error (500)" in {
        when(mockHttp.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, responseJson)))

        val response: Future[KnownFactsReturn] = enrolmentStoreProxyConnector.queryKnownFacts(saUtr, knownFacts)

        whenReady(response) { result =>
          result mustBe KnownFactsReturn("AB112233D", knownFactsResult = false)
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.failed(new Exception))
        val response: Future[KnownFactsReturn] = enrolmentStoreProxyConnector.queryKnownFacts(saUtr, knownFacts)

        whenReady(response) { result =>
          result mustBe KnownFactsReturn("AB112233D", knownFactsResult = false)
        }
      }

      "return false when no utr is passed" in {
        val saUtr = new SAUTR("")
        when(mockHttp.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.failed(new Exception))
        val response: Future[KnownFactsReturn] = enrolmentStoreProxyConnector.queryKnownFacts(saUtr, knownFacts)

        whenReady(response) { result =>
          result mustBe KnownFactsReturn("", knownFactsResult = false)
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

    "enrolForSa is called" should {
      val userId: String = "1212121212121"
      val utr: String = "1234"
      val groupId: String = "12121212"
      val enrolActivate: String = "enrolAndActivate"
      val saEnrolment = new SaEnrolment(userId, "enrolAndActivate")

      "return created when the call is successful (201)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(CREATED, "")))
        val response = enrolmentStoreProxyConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return no content if no utr found (204)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(NO_CONTENT, "")))
        val response = enrolmentStoreProxyConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return forbidden  if no utr found (403)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(FORBIDDEN, "")))
        val response = enrolmentStoreProxyConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return bad request  if no utr found (400)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))
        val response = enrolmentStoreProxyConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return internal server error if internal server error (500)" in {
        when(mockHttp.POST[SaEnrolment, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))

        val response = enrolmentStoreProxyConnector.enrolForSa(utr, userId, groupId, enrolActivate)

        whenReady(response) { result =>
          result mustBe false
        }
      }
    }
  }
}