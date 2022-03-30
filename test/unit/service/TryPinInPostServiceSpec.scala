package service

import connectors.{DataCacheConnector, TaxEnrolmentsConnector}
import controllers.ControllerSpecBase
import handlers.ErrorHandler
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.SAUTR
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{INTERNAL_SERVER_ERROR, SEE_OTHER}
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TryPinInPostServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockTaxEnrolmentsConnector: TaxEnrolmentsConnector = mock[TaxEnrolmentsConnector]
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]
  val btaOrigin: String = "bta-sa"

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)

  def service = new TryPinInPostService(
    mockDataCacheConnector,
    mockTaxEnrolmentsConnector,
    errorHandler,
    frontendAppConfig
  )

  "TryPinInPostService" when {
    "checkEnrol is called" must {
      "show the unsuccessful page when enrolForSa returns false and no returnUrl is provided" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), ArgumentMatchers.eq("enterSAUTR"))(any()))
          .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
        when(mockDataCacheConnector.getEntry[String](any(), ArgumentMatchers.eq("returnUrl"))(any()))
          .thenReturn(Future.successful(None))
        when(mockTaxEnrolmentsConnector.enrolForSa(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(false))

        val result = service.checkEnrol(btaOrigin)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "return a redirect to the unsuccessful page when enrolForSa returns false and origin is not ssttp-sa" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), ArgumentMatchers.eq("enterSAUTR"))(any()))
          .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
        when(mockTaxEnrolmentsConnector.enrolForSa(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(false))

        val result = service.checkEnrol(btaOrigin)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "return a redirect to the ssttp failure url when enrolForSa returns false and origin is ssttp-sa" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), ArgumentMatchers.eq("enterSAUTR"))(any()))
          .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
        when(mockTaxEnrolmentsConnector.enrolForSa(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(false))

        val result = service.checkEnrol("ssttp-sa")

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("http://localhost:9063/pay-what-you-owe-in-instalments/eligibility/not-enrolled")
      }

      "return a redirect to the Requested Access page when enrolForSa returns true" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), ArgumentMatchers.eq("enterSAUTR"))(any()))
          .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
        when(mockDataCacheConnector.getEntry[String](any(), ArgumentMatchers.eq("returnUrl"))(any()))
          .thenReturn(Future.successful(None))
        when(mockTaxEnrolmentsConnector.enrolForSa(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(true))

        val result = service.checkEnrol(btaOrigin)

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get mustBe controllers.sa.routes.RequestedAccessController.onPageLoad(btaOrigin).url
      }

      "return a redirect enrolment unsuccessful page when utr returns None" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
          .thenReturn(Future.successful(None))

        val result = service.checkEnrol(btaOrigin)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
      }
    }

}
