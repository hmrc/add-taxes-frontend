package connectors

import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import support.AddTaxesIntegrationTest
import support.stubs.StubVatSubscriptionConnector
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatSubscriptionConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: VatSubscriptionConnector = inject[VatSubscriptionConnector]

  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  val testVrn = "123456789"

  "VatSubscriptionConnector" when {

    "getMandationStatus" should {

      "return 200 when service returns 200" in {
        StubVatSubscriptionConnector.withResponseMandationStatus(testVrn)(OK)

        val result: Future[Int] = connector.getMandationStatus(testVrn)
        await(result) mustBe OK
        StubVatSubscriptionConnector.verifyMandationStatusCalled(1, testVrn)
      }

      "return NOT_FOUND when service returns 404" in {
        StubVatSubscriptionConnector.withResponseMandationStatus(testVrn)(NOT_FOUND)

        val result: Future[Int] = connector.getMandationStatus(testVrn)
        await(result) mustBe NOT_FOUND
        StubVatSubscriptionConnector.verifyMandationStatusCalled(1, testVrn)
      }

      "return PRECONDITION_FAILED when service returns 412" in {
        StubVatSubscriptionConnector.withResponseMandationStatus(testVrn)(PRECONDITION_FAILED)

        val result: Future[Int] = connector.getMandationStatus(testVrn)
        await(result) mustBe PRECONDITION_FAILED
        StubVatSubscriptionConnector.verifyMandationStatusCalled(1, testVrn)
      }

      "return INTERNAL_SERVER_ERROR when service throws an exception" in {
        StubVatSubscriptionConnector.throwExceptionMandationStatus(testVrn)

        val result: Future[Int] = connector.getMandationStatus(testVrn)
        await(result) mustBe INTERNAL_SERVER_ERROR
        StubVatSubscriptionConnector.verifyMandationStatusCalled(1, testVrn)
      }
    }
  }
}