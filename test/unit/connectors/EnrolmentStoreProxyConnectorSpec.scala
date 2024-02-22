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
import models.sa.{KnownFacts, KnownFactsAndIdentifiers, KnownFactsReturn, SAUTR}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.Future

class EnrolmentStoreProxyConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures {

  val mockHttp: HttpClient = mock[HttpClient]

  def enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = {
    new EnrolmentStoreProxyConnector(frontendAppConfig, mockHttp)
  }

  val utr: String = "1234"
  val officeNumber: String = "123"
  val payeReference: String = "AB123"

  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )
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
      |    "principalUserIds": [],
      |    "delegatedUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ]
      |}
      |""".stripMargin
  )

  val es3ResponseEnrolmentsWithSA =
    """
      |{
      |    "startRecord": 1,
      |    "totalRecords": 2,
      |    "enrolments": [
      |    {
      |           "service": "IR-CT",
      |           "state": "Activated",
      |           "friendlyName": "My Second Client's SA Enrolment",
      |           "enrolmentDate": "2017-06-25T12:24:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2017-07-01T09:52:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "9876543210"
      |              }
      |           ]
      |        },
      |        {
      |           "service": "IR-SA",
      |           "state": "Activated",
      |           "friendlyName": "My First Client's SA Enrolment",
      |           "enrolmentDate": "2018-10-05T14:48:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2018-10-13T17:36:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "1234567890"
      |              }
      |           ]
      |        },
      |        {
      |           "service": "IR-CT",
      |           "state": "Activated",
      |           "friendlyName": "My Second Client's SA Enrolment",
      |           "enrolmentDate": "2017-06-25T12:24:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2017-07-01T09:52:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "9876543210"
      |              }
      |           ]
      |        }
      |    ]
      |}
      |""".stripMargin

  val es3ResponseEnrolmentsWithOutSa =
    """
      |{
      |    "startRecord": 1,
      |    "totalRecords": 2,
      |    "enrolments": [
      |        {
      |           "service": "IR-PAYE",
      |           "state": "Activated",
      |           "friendlyName": "My First Client's SA Enrolment",
      |           "enrolmentDate": "2018-10-05T14:48:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2018-10-13T17:36:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "1234567890"
      |              }
      |           ]
      |        },
      |        {
      |           "service": "IR-CT",
      |           "state": "Activated",
      |           "friendlyName": "My Second Client's SA Enrolment",
      |           "enrolmentDate": "2017-06-25T12:24:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2017-07-01T09:52:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "9876543210"
      |              }
      |           ]
      |        }
      |    ]
      |}
      |""".stripMargin

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "EnrolmentStoreProxy" when {
    "checkExistingUtr is called" should {

      "return true when ES0 returns some principal enrolments" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, es0ResponsePrincipals, Map("" -> Seq.empty))))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr, "IR-SA")

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return false when ES0 returns only delegated enrolments" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, es0ResponseNoPrincipal, Map("" -> Seq.empty))))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr, "IR-SA")

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false if no utr found (204)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr, "IR-SA")

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false if internal server error (500)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr, "IR-SA")

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.failed(new Exception))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingUTR(utr, "IR-SA")

        whenReady(response) { result =>
          result mustBe false
        }
      }

    }

    "checkSaGroup is called" should {
      "return true when OK is returned from ES3 with SA" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, es3ResponseEnrolmentsWithSA,  Map("" -> Seq.empty))))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId, "IR-SA")
        whenReady(response) { res =>
          res mustBe true
        }
      }

      "return false when OK is returned from ES3 without SA" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, es3ResponseEnrolmentsWithOutSa,  Map("" -> Seq.empty))))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId, "IR-SA")
        whenReady(response) { res =>
          res mustBe false
        }
      }

      "return false when NO_CONTENT is returned from ES3" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, "")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId, "IR-SA")
        whenReady(response) { res =>
          res mustBe false
        }
      }

      "return false when INTERNAL_SERVER_ERROR is returned from ES3" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId, "IR-SA")
        whenReady(response) { res =>
          res mustBe false
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.failed(new Exception))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkSaGroup(groupId, "IR-SA")

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
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(OK, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe true
        }
      }

      "return false if no officeNumber, payeReference found (204)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(NO_CONTENT, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false if internal server error (500)" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(HttpResponse.apply(INTERNAL_SERVER_ERROR, "body")))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe false
        }
      }

      "return false when an exception occurs" in {
        when(mockHttp.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.failed(new Exception))

        val response: Future[Boolean] = enrolmentStoreProxyConnector.checkExistingEmpRef(officeNumber, payeReference)

        whenReady(response) { result =>
          result mustBe false
        }
      }
    }

  }
}
