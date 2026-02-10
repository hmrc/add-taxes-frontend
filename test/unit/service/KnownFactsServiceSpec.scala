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
import config.featureToggles.FeatureSwitch.{IvUpliftSwitch, VATKnownFactsCheck}
import config.featureToggles.FeatureToggleSupport
import connectors.{CitizensDetailsConnector, DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.ControllerSpecBase
import controllers.sa.{routes => saRoutes}
import handlers.ErrorHandler
import models.DesignatoryDetailsForKnownFacts
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsObject
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolments}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException, SessionId}
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import java.io.FileNotFoundException
import scala.concurrent.Future

class KnownFactsServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach with FeatureToggleSupport {

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("sessionId")))
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)
  val btaOrigin: String = "bta-sa"

  val mockSaService: SaService                                       = mock[SaService]
  val mockDataCacheConnector: DataCacheConnector                     = mock[DataCacheConnector]
  val mockEnrolmentStoreProxyConnector: EnrolmentStoreProxyConnector = mock[EnrolmentStoreProxyConnector]
  val mockAuditService: AuditService                                 = mock[AuditService]
  val mockAppConfig: FrontendAppConfig                               = mock[FrontendAppConfig]
  val mockCIDConnector: CitizensDetailsConnector                     = mock[CitizensDetailsConnector]
  val errorHandler: ErrorHandler                                     = injector.instanceOf[ErrorHandler]
  val testKnownFacts: KnownFacts                                     = KnownFacts(Some("AA00000A"), Some("AA1 1AA"), None)
  val testKnownFactsNinoOnly: KnownFacts                             = KnownFacts(None, Some("AA00000A"), None)
  val utr: SAUTR                                                     = SAUTR("1234567890")
  val vrn: String                                                    = "123456789"
  val sessId: String                                                 = "sessionId"

  def service(): KnownFactsService = new KnownFactsService(
    mockSaService,
    mockDataCacheConnector,
    mockEnrolmentStoreProxyConnector,
    mockAuditService,
    errorHandler,
    mockCIDConnector,
    mockAppConfig
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataCacheConnector, mockAuditService)
  }

  "knownFactsLocation" must {
    "return redirect to Try Pin in post when fails to retrieve UTR from dataCacheConnector" in {
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(None))

      val result = service().knownFactsLocation(testKnownFacts, btaOrigin)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin).url)

    }

    "return redirect to Try Pin in post when fails queryKnownFacts" in {
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = false)))

      val result = service().knownFactsLocation(testKnownFacts, btaOrigin)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin).url)

    }

    "return redirect to Iv when user passes queryKnownFacts" in {
      disable(IvUpliftSwitch)
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = true)))
      when(mockSaService.getIvRedirectLink(utr.value, btaOrigin)).thenReturn(Future.successful("/iv-redirect"))

      val result = service().knownFactsLocation(testKnownFacts, btaOrigin)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/iv-redirect")
    }

    "return redirect to Iv when user passes queryKnownFacts and all ninos match" in {
      enable(IvUpliftSwitch)
      implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L50, Some("AA00000A")),
        HtmlFormat.empty)
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())).thenReturn(Future.successful(Some(utr)))
      when(mockEnrolmentStoreProxyConnector.queryKnownFacts(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(KnownFactsReturn(utr.value, knownFactsResult = true)))
      when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
        .thenReturn(Future.successful(Some(DesignatoryDetailsForKnownFacts("AA00000A"))))
      when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/IvLink")

      val result = service().knownFactsLocation(testKnownFactsNinoOnly, btaOrigin)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/IvLink")
    }
  }

  "enrolmentCheck" must {
    "return CredIdFound and send and audit event when the ES0 connector returns true" in {
      when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(AuditResult.Success))
      when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any(), any())).thenReturn(Future.successful(true))

      await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.Yes)) mustBe CredIdFound
      verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
    }

    "return NoRecordFound and send and audit event when the ES0 and ES3 connectors returns false" in {
      when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(AuditResult.Success))
      when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any(), any())).thenReturn(Future.successful(false))
      when(mockEnrolmentStoreProxyConnector.checkSaGroup(any(), any())(any(), any(), any())).thenReturn(Future.successful(false))

      await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.Yes)) mustBe NoRecordFound
      verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
    }

    "return GroupIdFound and send and audit event when the ES0 connector returns false and ES3 connectors returns true" in {
      when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(AuditResult.Success))
      when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any(), any())).thenReturn(Future.successful(false))
      when(mockEnrolmentStoreProxyConnector.checkSaGroup(any(), any())(any(), any(), any())).thenReturn(Future.successful(true))

      await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.Yes)) mustBe GroupIdFound
      verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
    }

    "return NOSAUTR and send and audit event when the ES0 connector returns false and ES3 connectors returns true" in {
      when(mockAuditService.auditSA(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(AuditResult.Success))
      when(mockEnrolmentStoreProxyConnector.checkExistingUTR(any(), any())(any(), any(), any())).thenReturn(Future.successful(false))
      when(mockEnrolmentStoreProxyConnector.checkSaGroup(any(), any())(any(), any(), any())).thenReturn(Future.successful(true))

      await(service().enrolmentCheck("234", utr, "37219-dsjjd", Some("IR-SA"), DoYouHaveSAUTR.No)) mustBe NoSaUtr
      verify(mockAuditService, times(1)).auditSA(any(), any(), any())(any(), any(), any())
    }
  }

  "checkCIDNinoComparison" must {
    "redirect to the correct location" when {
      "user has auth nino, details nino and knownfact nino are all the same" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, Some("AA00000A")),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(Some(DesignatoryDetailsForKnownFacts("AA00000A"))))
        when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/IvLink")

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000A")

        redirectLocation(result) mustBe Some("/IvLink")
      }

      "user has 50 confidence level and utr has the same nino as the account and enrol fails" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L50, Some("AA00000A")),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(Some(DesignatoryDetailsForKnownFacts("AA00000A"))))
        when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/IvLink")

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000A")

        redirectLocation(result) mustBe Some("/IvLink")
      }

      "user has 50 confidence level and utr has the same nino as the account and getDesignatoryDetails throws a NotFound exception" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L50, Some("AA00000A")),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.failed(new NotFoundException("Citizen details service threw NotFound exception")))
        when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/try-pin-in-post")

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000A")

        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/try-pin-in-post?status=LockedOut&origin=bta-sa")
      }

      "user has 50 confidence level and utr has the same nino as the account and getDesignatoryDetails throws a non-NotFound exception" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L50, Some("AA00000A")),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.failed(new FileNotFoundException("General exception")))
        when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/try-pin-in-post")

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000A")
        status(result) mustBe INTERNAL_SERVER_ERROR

      }

      "user has 200 confidence level and utr does not have the same nino as the account" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, Some("AA00000A")),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(Some(DesignatoryDetailsForKnownFacts("AA00000B"))))
        when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/IvLink")

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000A")

        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/retry-known-facts?origin=bta-sa")
      }

      "user has 200 confidence level and nino input does not have the same nino as the account and the utr" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, Some("AA00000A")),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(Some(DesignatoryDetailsForKnownFacts("AA00000A"))))
        when(mockAppConfig.ivUpliftUrl(any())).thenReturn("/IvLink")

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000B")

        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/retry-known-facts?origin=bta-sa")
      }

      "user has 200 confidence level call to CID fails" in {
        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, ConfidenceLevel.L200, None),
          HtmlFormat.empty)
        when(mockCIDConnector.getDesignatoryDetailsForKnownFacts(any(), any())(any(), any()))
          .thenReturn(Future.successful(None))

        val result = service().checkCIDNinoComparison("bta-sa", "1234567891", "AA00000A")

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/retry-known-facts?origin=bta-sa")
      }
    }
  }

  "checkVrnMatchesPreviousAttempts" when {
    "'VATKnownFactsCheck' switch is disabled" must {
      "return the given VRN in a Right" in {
        disable(VATKnownFactsCheck)
        val result = await(service().checkVrnMatchesPreviousAttempts(vrn))

        result mustBe Right(vrn)
      }
    }

    "'VATKnownFactsCheck' switch is enabled" when {
      "there is no previous stored VRN" must {
        "save the VRN and return it in a Right" in {
          enable(VATKnownFactsCheck)
          mockSaveVrn()
          mockRetrieveVrn(savedVrn = None)

          val result = await(service().checkVrnMatchesPreviousAttempts(vrn))
          result mustBe Right(vrn)
          verify(mockDataCacheConnector, times(1)).save[String](any(), any(), any())(any())
        }
      }
      "the previously stored VRN matches the new submission" must {
        "return the VRN in a Right without sending an audit" in {
          enable(VATKnownFactsCheck)
          mockRetrieveVrn(savedVrn = Some(vrn))

          val result = await(service().checkVrnMatchesPreviousAttempts(vrn))
          result mustBe Right(vrn)
          verify(mockDataCacheConnector, never())
            .save[String](any(), any(), any())(any())

        }
      }
      "the previously stored VRN does NOT match the new submission" must {
        "send an audit and redirect to error page with session sign out in a Left" in {
          enable(VATKnownFactsCheck)
          mockRetrieveVrn(savedVrn = Some("different VRN"))
          val cveErrorPageUrl = "/claim-vat-enrolment/error/different-vat-registration-numbers"
          when(mockAppConfig.addTaxesSignoutThenContinueTo(any())).thenReturn(cveErrorPageUrl)

          val result         = await(service().checkVrnMatchesPreviousAttempts(vrn))
          val resultRedirect = result.left.map(_.header.headers("Location"))
          resultRedirect mustBe Left(cveErrorPageUrl)
          verify(mockAuditService, times(1))
            .auditCveMultipleVrnsAttempted(any(), any(), any())(any(), any(), any())
        }
      }
    }
  }

  private def mockRetrieveVrn(savedVrn: Option[String]): OngoingStubbing[Future[Option[String]]] =
    when(mockDataCacheConnector.getEntry[String](any(), any())(any())).thenReturn(Future.successful(savedVrn))
  private def mockSaveVrn(): OngoingStubbing[Future[CacheMap]] =
    when(mockDataCacheConnector.save[String](any(), any(), any())(any())).thenReturn(Future.successful(CacheMap("credId", Map("" -> JsObject.empty))))

}
