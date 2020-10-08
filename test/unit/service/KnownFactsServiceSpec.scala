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

import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.Assets.SEE_OTHER
import controllers.ControllerSpecBase
import controllers.sa.{EnrolmentSuccessController, routes => saRoutes}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{KnownFacts, KnownFactsReturn, SAUTR}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class KnownFactsServiceSpec extends ControllerSpecBase with MockitoSugar {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
    HtmlFormat.empty)

  val mockEnrolForSaService: EnrolForSaService = mock[EnrolForSaService]
  val mockSaService: SaService = mock[SaService]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockEnrolmentSuccessController: EnrolmentSuccessController = mock[EnrolmentSuccessController]

  val testKnownFacts: KnownFacts = KnownFacts(Some("AA00000A"), Some("AA1 1AA"))
  val utr: SAUTR = SAUTR("1234567890")

  def service(): KnownFactsService = new KnownFactsService(
    mockEnrolForSaService,
    mockSaService,
    mockDataCacheConnector,
    mockEnrolmentStoreProxyConnector,
    mockEnrolmentSuccessController
  )

  "KnownFacts service" must {

    "return redirect to Try Pin in post when fails to retrieve UTR from dataCacheConnector" in {
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(None))

      val result = service().knownFactsLocation(testKnownFacts)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad().url)

    }

    "return redirect to Try Pin in post when fails queryKnownFacts" in {
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = false)))

      val result = service().knownFactsLocation(testKnownFacts)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad().url)

    }

    "enrol for SA and redirect to try pin and post when user passes queryKnownFacts and is CL200, enrol is not CREATED" in {
      implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200),
        HtmlFormat.empty)

      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(SAUTR("1234567890"))))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn("1234567890", knownFactsResult = true)))
      when(mockEnrolForSaService.enrolForSa(any(),any(), any())(any(), any(), any())).thenReturn(Future.successful(false))

      val result = service().knownFactsLocation(testKnownFacts)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad().url)

    }

    "return redirect to Iv when user passes queryKnownFacts and is < CL200" in {
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = true)))
      when(mockSaService.getIvRedirectLink(utr.value)).thenReturn(Future.successful("/iv-redirect"))

      val result = service().knownFactsLocation(testKnownFacts)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/iv-redirect")
    }

    "redirect to enrolment Success when user passes queryKnownFacts and is CL200 and SaEnrolment is created" in {
      implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200),
        HtmlFormat.empty)

      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = true)))
      when(mockEnrolForSaService.enrolForSa(any(),any(), any())(any(), any(), any())).thenReturn(Future.successful(true))

      val result = service().knownFactsLocation(testKnownFacts)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad().url)

    }
  }

}
