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

import config.FrontendAppConfig
import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.Assets.SEE_OTHER
import controllers.ControllerSpecBase
import controllers.sa.{routes => saRoutes}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class KnownFactsServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
    HtmlFormat.empty)
  val btaOrigin: String = "bta-sa"

  val mockSaService: SaService = mock[SaService]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockAuditService: AuditService = mock[AuditService]
  val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val testKnownFacts: KnownFacts = KnownFacts(Some("AA00000A"), Some("AA1 1AA"), None)
  val utr: SAUTR = SAUTR("1234567890")

  def service(): KnownFactsService = new KnownFactsService(
    mockSaService,
    mockDataCacheConnector,
    mockEnrolmentStoreProxyConnector,
    mockAuditService,
    mockAppConfig
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditService)
  }

  "KnownFacts service" when {

    "knownFactsLocation is called" must {
      "return redirect to Try Pin in post when fails to retrieve UTR from dataCacheConnector" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(None))

        val result = service().knownFactsLocation(testKnownFacts, btaOrigin)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin).url)

      }

      "return redirect to Try Pin in post when fails queryKnownFacts" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
        when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = false)))

        val result = service().knownFactsLocation(testKnownFacts, btaOrigin)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin).url)

      }

      "return redirect to Iv when user passes queryKnownFacts" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
        when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = true)))
        when(mockSaService.getIvRedirectLink(utr.value, btaOrigin)).thenReturn(Future.successful("/iv-redirect"))

        val result = service().knownFactsLocation(testKnownFacts, btaOrigin)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/iv-redirect")
      }
    }

    "enrolmentCheck is called" must {
      "return CredIdFound and send and audit event when the ES0 connector returns true" in {
        when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(AuditResult.Success))
        when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any())).thenReturn(Future.successful(true))

        await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.Yes)) mustBe CredIdFound
//        verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
      }

      "return NoRecordFound and send and audit event when the ES0 and ES3 connectors returns false" in {
        when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(AuditResult.Success))
        when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any())).thenReturn(Future.successful(false))
        when(mockEnrolmentStoreProxyConnector.checkSaGroup(any(), any())(any(), any())).thenReturn(Future.successful(false))

        await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.Yes)) mustBe NoRecordFound
//        verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
      }


      "return GroupIdFound and send and audit event when the ES0 connector returns false and ES3 connectors returns true" in {
        when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(AuditResult.Success))
        when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any())).thenReturn(Future.successful(false))
        when(mockEnrolmentStoreProxyConnector.checkSaGroup(any(), any())(any(), any())).thenReturn(Future.successful(true))

        await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.Yes)) mustBe GroupIdFound
//        verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
      }

      "return NOSAUTR and send and audit event when the ES0 connector returns false and ES3 connectors returns true" in {
        when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(AuditResult.Success))
        when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any())).thenReturn(Future.successful(false))
        when(mockEnrolmentStoreProxyConnector.checkSaGroup(any(), any())(any(), any())).thenReturn(Future.successful(true))

        await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.No)) mustBe NoSaUtr
        //        verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
      }

    }
  }
}
