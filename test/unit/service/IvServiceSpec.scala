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

import connectors.{DataCacheConnector, IvConnector, TaxEnrolmentsConnector}
import controllers.ControllerSpecBase
import controllers.sa.{routes => saRoutes}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{IvLinks, JourneyLinkResponse, SAUTR, SaEnrolmentDetails}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class IvServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  val mockTaxEnrolmentsConnector: TaxEnrolmentsConnector = mock[TaxEnrolmentsConnector]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockIvConnector: IvConnector = mock[IvConnector]
  val enrolActivate: String = "enrolAndActivate"
  val btaOrigin: String = "bta-sa"
  val ssttpOrigin: String = "ssttp-sa"
  val journeyId: String = "12345"

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)

  implicit val requestWithJson: ServiceInfoRequest[JsValue] = ServiceInfoRequest[JsValue](
    AuthenticatedRequest(FakeRequest().withBody(Json.parse(s"""{}""")), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)

  def verifyDataCacheSave(expectedTimes: Int): Unit =
    verify(mockDataCacheConnector, times(expectedTimes)).save(any(), any(), any())(any())

  def service() = new IvService(
    mockDataCacheConnector,
    mockIvConnector,
    mockTaxEnrolmentsConnector
  )

  def serviceWithStubbedLinkCheck(journeyLinkCheckResult: String): IvService = new IvService(
    mockDataCacheConnector,
    mockIvConnector,
    mockTaxEnrolmentsConnector
  ) {
    override def journeyLinkCheck()
                                 (implicit request: ServiceInfoRequest[AnyContent],
                                  ec: ExecutionContext,
                                  hc: HeaderCarrier): Future[String] = {
      Future.successful(journeyLinkCheckResult)
    }
  }


  def serviceWithStubbedLinkCheckUplift(journeyLinkCheckResult: String, journeyId: String): IvService = new IvService(
    mockDataCacheConnector,
    mockIvConnector,
    mockTaxEnrolmentsConnector
  ) {

    override def journeyLinkCheckUplift(journeyId: String)
                                       (implicit ec: ExecutionContext,
                                        hc: HeaderCarrier): Future[String] = {
      Future.successful(journeyLinkCheckResult)
    }
  }



  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
  }

  "Iv service" when {
    "journeyLinkCheck is called" must {
      " returns false when data cache connector returns no iv link" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any())).thenReturn(Future.successful(None))
        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe "Failed"
        }
      }

      "returns false when iv connector throws an exception" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any()))
          .thenReturn(Future.successful(Some(IvLinks("link", "journeyLink"))))
        when(mockIvConnector.checkJourneyLink(any())(any(), any())).thenReturn(Future.failed(new Exception("")))

        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe "Failed"
        }
      }

      "returns true when iv connector returns a Success result" in {
        when(mockDataCacheConnector.getEntry[IvLinks](any(), any())(any()))
          .thenReturn(Future.successful(Some(IvLinks("link", "journeyLink"))))
        when(mockIvConnector.checkJourneyLink(any())(any(), any()))
          .thenReturn(Future.successful(JourneyLinkResponse("Success", "0")))

        val result = service().journeyLinkCheck()

        whenReady(result) { result =>
          result mustBe "Success"
        }
      }
    }

    "journeyLinkCheckUplift is called" must {
      "returns Failed when journey link check returns a Failed response" in {
        when(mockIvConnector.checkJourneyLinkUplift(any())(any(), any()))
          .thenReturn(Future.successful(JourneyLinkResponse("Failed", "test")))
        val result = service().journeyLinkCheckUplift("12345")

        whenReady(result) { result =>
          result mustBe "Failed"
        }
      }

      "returns Failed when journey link check returns a error response" in {
        when(mockIvConnector.checkJourneyLinkUplift(any())(any(), any()))
        .thenReturn(Future.failed(new Exception))
        val result = service().journeyLinkCheckUplift("12345")

        whenReady(result) { result =>
          result mustBe "Failed"
        }
      }

      "returns Success when journey link check returns a Success response" in {
        when(mockIvConnector.checkJourneyLinkUplift(any())(any(), any()))
          .thenReturn(Future.successful(JourneyLinkResponse("Success", "0")))

        val result = service().journeyLinkCheckUplift("12345")

        whenReady(result) { result =>
          result mustBe "Success"
        }
      }
    }
  }

    "ivCheckAndEnrol is called" must {
      "return try pin an post call" when {
        "journeyLink returns false" in {
          val result = serviceWithStubbedLinkCheck("Failed").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), btaOrigin).url)
        }

        "data cache connector returns None and journeyLink is true" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(None))

          val result = serviceWithStubbedLinkCheck("Success").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), btaOrigin).url)
        }

        "enrol for sa returns false" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
          when(mockTaxEnrolmentsConnector.enrolForSa(any(), any(), any(), any())(any(), any()))
            .thenReturn(Future.successful(false))

          val result = serviceWithStubbedLinkCheck("Success").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), btaOrigin).url)

        }

        "Journey link result is InsufficientEvidence" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheck("InsufficientEvidence").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }

        "Journey link result is LockedOut" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheck("LockedOut").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), btaOrigin).url)
        }

        "Journey link result is FailedIV" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheck("FailedIV").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }

        "Journey link result is PreconditionFailed" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheck("PreconditionFailed").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }

        "Journey link result is FailedMatching" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheck("FailedMatching").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }
      }
      "return enrolment successful call" when {
        "journeyLink returns true and enrol for sa returns true" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
          when(mockTaxEnrolmentsConnector.enrolForSa(any(), any(), any(), any())(any(), any()))
            .thenReturn(Future.successful(true))

          val result = serviceWithStubbedLinkCheck("Success").ivCheckAndEnrol(btaOrigin)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin).url)

        }
      }
    }


    "ivCheckAndEnrolUplift is called" must {
      "return try pin an post call" when {
        "journeyLink returns false" in {
          val result = serviceWithStubbedLinkCheckUplift("Failed", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), btaOrigin).url)
        }

        "data cache connector returns None and journeyLink is true" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(None))

          val result = serviceWithStubbedLinkCheckUplift("Success", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), btaOrigin).url)
        }

        "enrol for sa returns false" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
          when(mockTaxEnrolmentsConnector.enrolForSa(any(), any(), any(), any())(any(), any()))
            .thenReturn(Future.successful(false))

          val result = serviceWithStubbedLinkCheckUplift("Success", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("Failed"), btaOrigin).url)

        }

        "Journey link result is InsufficientEvidence" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheckUplift("InsufficientEvidence", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }

        "Journey link result is LockedOut" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheckUplift("LockedOut", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("LockedOut"), btaOrigin).url)
        }

        "Journey link result is FailedIV" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheckUplift("FailedIV", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }

        "Journey link result is PreconditionFailed" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheckUplift("PreconditionFailed", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }

        "Journey link result is FailedMatching" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))

          val result = serviceWithStubbedLinkCheckUplift("FailedMatching", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), btaOrigin).url)
        }
      }
      "return enrolment successful call" when {
        "journeyLink returns true and enrol for sa returns true" in {
          when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
            .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
          when(mockTaxEnrolmentsConnector.enrolForSa(any(), any(), any(), any())(any(), any()))
            .thenReturn(Future.successful(true))

          val result = serviceWithStubbedLinkCheckUplift("Success", journeyId).ivCheckAndEnrolUplift(btaOrigin, journeyId)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin).url)

        }
      }
    }

    "journeyRouter is called" must {
      "return try-iv url" when {
        "a utr is provided" in  {
          val result = service().journeyRouter(SaEnrolmentDetails(Some("1234567890"), "pta-sa", "12345"))
          await(result) mustBe "/business-account/add-tax/self-assessment/try-iv?origin=pta-sa"
          verifyDataCacheSave(1)
        }
      }

      "return enter-sa-utr url" when {

        "no utr is provided" in {
          val result = service().journeyRouter(SaEnrolmentDetails(None, "pta-sa", "12345"))
          await(result) mustBe "/business-account/add-tax/self-assessment/enter-sa-utr?origin=pta-sa"
          verifyDataCacheSave(1)
        }
      }
    }

}

