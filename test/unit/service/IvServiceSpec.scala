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

import connectors.{DataCacheConnector, IvConnector}
import controllers.ControllerSpecBase
import controllers.sa.{EnrolmentSuccessController, TryPinInPostController, routes => saRoutes}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{IvLinks, JourneyLinkResponse, SAUTR}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class IvServiceSpec extends ControllerSpecBase with MockitoSugar {

  val mockEnrolForSaService: EnrolForSaService = mock[EnrolForSaService]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockIvConnector: IvConnector = mock[IvConnector]

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
    HtmlFormat.empty)

  def service() = new IvService(
    mockDataCacheConnector,
    mockIvConnector,
    mockEnrolForSaService
  )

  def serviceWithStubbedLinkCheck(journeyLinkCheckResult: Boolean): IvService = new IvService(
    mockDataCacheConnector,
    mockIvConnector,
    mockEnrolForSaService
  ) {
    override def journeyLinkCheck()
                                 (implicit request: ServiceInfoRequest[AnyContent],
                                  ec: ExecutionContext,
                                  hc: HeaderCarrier): Future[Boolean] = {
      Future.successful(journeyLinkCheckResult)
    }
  }

  "Iv service" when {
    "journeyLinkCheck is called" must {
      " returns false when data cache connector returns no iv link" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any())).thenReturn(Future.successful(None))
        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe false
        }
      }

      "returns false when iv connector throws an exception" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any()))
          .thenReturn(Future.successful(Some(IvLinks("link", "journeyLink"))))
        when(mockIvConnector.checkJourneyLink(any())(any(), any())).thenReturn(Future.failed(new Exception("")))

        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe false
        }
      }

      "returns false when iv connector returns a result that is not Success" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any()))
          .thenReturn(Future.successful(Some(IvLinks("link", "journeyLink"))))
        when(mockIvConnector.checkJourneyLink(any())(any(), any()))
          .thenReturn(Future.successful(JourneyLinkResponse("NONE", "0")))

        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe false
        }
      }

      "returns true when iv connector returns a Success result" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any()))
          .thenReturn(Future.successful(Some(IvLinks("link", "journeyLink"))))
        when(mockIvConnector.checkJourneyLink(any())(any(), any()))
          .thenReturn(Future.successful(JourneyLinkResponse("Success", "0")))

        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe true
        }
      }
    }

    "ivCheckAndEnrol is called" must {
      "return try pin an post call" when {
        "journeyLink returns false" in {
          val result = serviceWithStubbedLinkCheck(false).ivCheckAndEnrol()
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad().url)
        }

        "data cache connector returns None and journeyLink is true" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(None))

          val result = serviceWithStubbedLinkCheck(true).ivCheckAndEnrol()
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad().url)
        }

        "enrol for sa returns false" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
          when(mockEnrolForSaService.enrolForSa(any(), any(), any())(any(), any(), any()))
            .thenReturn(Future.successful(false))

          val result = serviceWithStubbedLinkCheck(true).ivCheckAndEnrol()
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad().url)

        }
      }
      "return enrolment successful call" when {
        "journeyLink returns true and enrol for sa returns true" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
          when(mockEnrolForSaService.enrolForSa(any(), any(), any())(any(), any(), any()))
            .thenReturn(Future.successful(true))

          val result = serviceWithStubbedLinkCheck(true).ivCheckAndEnrol()
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad().url)
        }
      }
    }
  }

}

