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

import connectors.{CitizensDetailsConnector, DataCacheConnector, SaConnector, TaxEnrolmentsConnector}
import controllers.ControllerSpecBase
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{IvLinks, YourSaIsNotInThisAccount}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import controllers.sa.{routes => saRoutes}
import handlers.ErrorHandler
import models.DesignatoryDetails

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class SaServiceSpec extends ControllerSpecBase with MockitoSugar {

  val mockSaConnector: SaConnector = mock[SaConnector]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockCIDConnector: CitizensDetailsConnector = mock[CitizensDetailsConnector]
  val mockTaxEnrolConnector: TaxEnrolmentsConnector = mock[TaxEnrolmentsConnector]
  val testIvLinks: IvLinks = IvLinks("/iv-link", "/journeyLink")
  val btaOrigin: String = "bta-sa"
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]


  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)

  def service() = new SaService(
    mockSaConnector,
    mockDataCacheConnector,
    mockCIDConnector,
    mockTaxEnrolConnector,
    errorHandler,
    frontendAppConfig
  )


  "Sa service" when {
    "getIvRedirectLink is called" must {
      "return redirect url of Try Pin in post when fails to retrieve iv links" in {
        when(mockSaConnector.getIvLinks(any(), any())(any(), any())).thenReturn(Future.successful(None))
        when(mockDataCacheConnector.getEntry[String](any(), any())(any()))
          .thenReturn(Future.successful(None))

        val result = service().getIvRedirectLink("1234567890", btaOrigin)

        whenReady(result) { result =>
          result mustBe saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url
        }
      }

      "return redirect url from retrieve iv links" in {
        when(mockSaConnector.getIvLinks(any(), any())(any(), any())).thenReturn(Future.successful(Some(testIvLinks)))
        when(mockDataCacheConnector.getEntry[String](any(), any())(any()))
          .thenReturn(Future.successful(None))

        val result = service().getIvRedirectLink("1234567890", btaOrigin)

        whenReady(result) { result =>
          result must include("/iv-link")
        }
      }
    }

    "yourSaIsNotInThisAccount is called" must {
      "redirect to the correct location" when {
        "user chooses to look in another account" in {
          val result = service().yourSaIsNotInThisAccount(YourSaIsNotInThisAccount.LookInOtherAccount, "bta-sa")
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some("http://localhost:9020/business-account/wrong-credentials")
        }
        "user chooses to add to this account" in {
          val result = service().yourSaIsNotInThisAccount(YourSaIsNotInThisAccount.AddToThisAccount, "bta-sa")
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment?origin=bta-sa")
        }
      }
    }

    "checkCIDNinoComparison is called" must {
      "redirect to the correct location" when {
        "user has 200 confidence level and utr has the same nino as the account and enrol successfully" in {
          implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
            AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, Some("AA00000A")),
            HtmlFormat.empty)
          when(mockCIDConnector.getDesignatoryDetails(any(), any())(any()))
            .thenReturn(Future.successful(Some(DesignatoryDetails("test", "test", "AA00000A", "test"))))
          when(mockTaxEnrolConnector.enrolForSa(any(), any(), any(), any())(any(), any()))
            .thenReturn(Future.successful(true))

          val result = service().checkCIDNinoComparison("bta-sa", "1234567891")

          await(result) mustBe Some("/business-account/add-tax/self-assessment/enrolment-successful?origin=bta-sa")
        }

        "user has 200 confidence level and utr has the same nino as the account and enrol fails" in {
          implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
            AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, Some("AA00000A")),
            HtmlFormat.empty)
          when(mockCIDConnector.getDesignatoryDetails(any(), any())(any()))
            .thenReturn(Future.successful(Some(DesignatoryDetails("test", "test", "AA00000A", "test"))))
          when(mockTaxEnrolConnector.enrolForSa(any(), any(), any(), any())(any(), any()))
            .thenReturn(Future.successful(false))

          val result = service().checkCIDNinoComparison("bta-sa", "1234567891")

          await(result) mustBe Some("/business-account/add-tax/self-assessment/try-pin-in-post?status=Failed&origin=bta-sa")
        }

        "user has 200 confidence level and utr does not have the same nino as the account" in {
          implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
            AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, Some("AA00000A")),
            HtmlFormat.empty)
          when(mockCIDConnector.getDesignatoryDetails(any(), any())(any()))
            .thenReturn(Future.successful(Some(DesignatoryDetails("test", "test", "AA00000B", "test"))))

          val result = service().checkCIDNinoComparison("bta-sa", "1234567891")

          await(result) mustBe Some("/business-account/add-tax/self-assessment/retry-known-facts?origin=bta-sa")
        }

        "user has 50 confidence level" in {
          implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
            AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L50, None),
            HtmlFormat.empty)

          val result = service().checkCIDNinoComparison("bta-sa", "1234567891")

          await(result) mustBe None
        }

        "user has 200 confidence level call to CID fails" in {
          implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
            AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, None),
            HtmlFormat.empty)
          when(mockCIDConnector.getDesignatoryDetails(any(), any())(any()))
            .thenReturn(Future.successful(None))

          val result = service().checkCIDNinoComparison("bta-sa", "1234567891")

          await(result) mustBe Some("CidError")
        }
      }
    }
  }
}
