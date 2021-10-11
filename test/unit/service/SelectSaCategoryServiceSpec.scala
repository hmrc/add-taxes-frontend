package service

import connectors.DataCacheConnector
import controllers.Assets.SEE_OTHER
import controllers.ControllerSpecBase
import handlers.ErrorHandler
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{status, _}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.{Individual, Organisation}
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SelectSaCategoryServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def test(affinityGroup: AffinityGroup) = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(affinityGroup), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)

  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockKnownFactsService: KnownFactsService = mock[KnownFactsService]
  val mockAuditService: AuditService = mock[AuditService]
  val mockSaService: SaService = mock[SaService]
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  val btaOrigin: String = "bta-sa"

  class Setup {

    implicit val request = test(Organisation)

    val testService: SelectSaCategoryService = new SelectSaCategoryService(
      mockDataCacheConnector,
      mockKnownFactsService,
      mockSaService,
      frontendAppConfig,
      errorHandler,
      mockAuditService) {override val accessMtdFeatureSwitch: Boolean = false}
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  "SelectSaCategory Service" when {
    "Category SA is Selected" must {
      "return redirect to are you self employed page when no record is found and an individual" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        override implicit val request: ServiceInfoRequest[AnyContent] = test(Individual)

        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/self-employed")

      }

      "return redirect to select taxes page when NoSaUtr is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoSaUtr))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("portal/business-registration/select-taxes")

      }

      "return redirect to knownfacts page when no Record is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))

        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/known-facts?origin=bta-sa")

      }

      "return redirect to result location when checkCIDNinoComparison returns a result" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(Some("/redirectUrl")))


        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/redirectUrl")
      }

      "return Internal Server Error when checkCIDNinoComp returns CidError" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(Some("CidError")))


        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "return redirect to wrong credentials page when credId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(CredIdFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))

        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("business-account/wrong-credentials")

      }

      "return redirect to group error page when groupId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(GroupIdFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))

        val result = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/group-error")

      }
    }

    "Category Partnership is Selected" must {
      "return redirect to wrong credential page when credId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(CredIdFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("business-account/wrong-credentials")

      }

      "return redirect to group error page when groupId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(GroupIdFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/group-error")

      }

      "return redirect Do you want to add a partner page when NoRecord is found and an Organisation" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

      }

      "return redirect setup a new account page when NoRecord is found and an Individual" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        override implicit val request: ServiceInfoRequest[AnyContent] = test(Individual)
        val result = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership/new-account")

      }
    }

    "Category Trust is Selected" must {
      "return redirect to wrong credential page when credId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(CredIdFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("business-account/wrong-credentials")

      }

      "return redirect to group error page when groupId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(GroupIdFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/group-error")

      }

      "return redirect Do you want to add a partner page when NoRecord is found and an Organisation" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        val result = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/trust")

      }

      "return redirect setup a new account page when NoRecord is found and an Individual" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        when(mockSaService.checkCIDNinoComparison(any(), any())(any(), any(), any())).thenReturn(Future.successful(None))
        override implicit val request: ServiceInfoRequest[AnyContent] = test(Individual)
        val result = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/trust/new-account")

      }
    }
  }
}
