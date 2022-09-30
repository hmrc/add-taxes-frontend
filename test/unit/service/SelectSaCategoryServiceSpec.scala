package service

import connectors.DataCacheConnector
import controllers.ControllerSpecBase
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContent, Result}
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

  def test(affinityGroup: AffinityGroup): ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(affinityGroup), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)

  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockKnownFactsService: KnownFactsService = mock[KnownFactsService]
  val mockAuditService: AuditService = mock[AuditService]

  val btaOrigin: String = "bta-sa"

  class Setup {

    implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation)

    val testService: SelectSaCategoryService = new SelectSaCategoryService(
      mockDataCacheConnector,
      mockKnownFactsService,
      mockAuditService,
      frontendAppConfig) {override val accessMtdFeatureSwitch: Boolean = false}
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
        override implicit val request: ServiceInfoRequest[AnyContent] = test(Individual)

        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/self-employed")

      }

      "return redirect to select taxes page when NoSaUtr is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoSaUtr))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("portal/business-registration/select-taxes")

      }

      "return redirect to knownfacts page when no Record is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))

        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/known-facts?origin=bta-sa")

      }

      "return redirect to wrong credentials page when credId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(CredIdFound))

        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("business-account/wrong-credentials")

      }

      "return redirect to group error page when groupId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567800")))
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(GroupIdFound))

        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Sa, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/group-error")

      }
    }

    "Category Partnership is Selected" must {
      "return redirect to wrong credential page when credId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(CredIdFound))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("business-account/wrong-credentials")

      }

      "return redirect to group error page when groupId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(GroupIdFound))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/group-error")

      }

      "return redirect Do you want to add a partner page when NoRecord is found and an Organisation" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

      }

      "return redirect setup a new account page when NoRecord is found and an Individual" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        override implicit val request: ServiceInfoRequest[AnyContent] = test(Individual)
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Partnership, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership/new-account")

      }
    }

    "Category Trust is Selected" must {
      "return redirect to wrong credential page when credId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(CredIdFound))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.Yes, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must include("business-account/wrong-credentials")

      }

      "return redirect to group error page when groupId is found" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(GroupIdFound))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/group-error")

      }

      "return redirect Do you want to add a partner page when NoRecord is found and an Organisation" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/trust")

      }

      "return redirect setup a new account page when NoRecord is found and an Individual" in new Setup {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        when(mockKnownFactsService.enrolmentCheck(any(), any(), any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(NoRecordFound))
        override implicit val request: ServiceInfoRequest[AnyContent] = test(Individual)
        val result: Future[Result] = testService.saCategoryResult(SelectSACategory.Trust, DoYouHaveSAUTR.No, btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/trust/new-account")

      }
    }
  }
}
