package service

import connectors.DataCacheConnector
import controllers.Assets.{InternalServerError, SEE_OTHER}
import controllers.ControllerSpecBase
import handlers.ErrorHandler
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.SAUTR
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.INTERNAL_SERVER_ERROR
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
  val mockEnrolForSaService: EnrolForSaService = mock[EnrolForSaService]
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
    HtmlFormat.empty)

  def service = new TryPinInPostService(
    mockDataCacheConnector,
    mockEnrolForSaService,
    errorHandler
  )

  "TryPinInPostService" when {
    "checkEnrol is called" must {
      "return a redirect to the unsuccessful page when enrolForSa returns false" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
          .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
        when(mockEnrolForSaService.enrolForSa(any(), any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(false))

        val result = service.checkEnrol()

        status(result) mustBe INTERNAL_SERVER_ERROR
      }

      "return a redirect to the Requested Access page when enrolForSa returns true" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
          .thenReturn(Future.successful(Some(SAUTR("1234567890"))))
        when(mockEnrolForSaService.enrolForSa(any(), any(), any(), any())(any(), any()))
          .thenReturn(Future.successful(true))

        val result = service.checkEnrol()

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get mustBe controllers.sa.routes.RequestedAccessController.onPageLoad().url
      }

      "return a redirect enrolment unsuccessful page when utr returns None" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any()))
          .thenReturn(Future.successful(None))

        val result = service.checkEnrol()

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
      }
    }

}
