package connectors

import models.BusinessDetails
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.IvLinks
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, _}
import play.twirl.api.HtmlFormat
import support.AddTaxesIntegrationTest
import support.stubs.StubSaConnector
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SaConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: SaConnector = inject[SaConnector]
  val testUtr = "1234567890"
  val origin = "bta-sa"
  val testNino = "aa000000a"
  val testIdentifier = "nino"

  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  "SaConnector" when {
    "getIvLinks" should {
      "returns IvLinks for a successful call" in {
        StubSaConnector.successfulLinkRetrieval(testUtr, origin)
        val result: Future[Option[IvLinks]] = connector.getIvLinks(testUtr, origin)

        await(result) mustBe Some(IvLinks("/mdtp/confirm/proxy/some-iv-link", "/mdtp/journey/journeyId/iv-stub-data"))
        StubSaConnector.verifyGetIvLinks(1, testUtr, origin)
      }

      "returns None when an exception is returned from Iv" in {
        StubSaConnector.unSuccessfulLinkRetrieval(testUtr, origin)
        val result: Future[Option[IvLinks]] = connector.getIvLinks(testUtr, origin)

        await(result) mustBe None
        StubSaConnector.verifyGetIvLinks(1, testUtr, origin)
      }
    }

    "getBusinessDetails" should {
      "returns BusinessDetails for a successful call" in {
        StubSaConnector.successfulDetailsRetrieval(testNino, testIdentifier)
        val result: Future[Option[BusinessDetails]] = connector.getBusinessDetails(testNino, testIdentifier)

        await(result) mustBe Some(BusinessDetails("aa000000a", "1234567"))
        StubSaConnector.verifyDetailsRetrieval(1, testNino, testIdentifier)
      }

      "returns None when an exception is returned from DES" in {
        StubSaConnector.unSuccessfulDetailsRetrieval(testNino, testIdentifier)
        val result: Future[Option[BusinessDetails]] = connector.getBusinessDetails(testNino, testIdentifier)

        await(result) mustBe None
        StubSaConnector.verifyDetailsRetrieval(1, testNino, testIdentifier)
      }
    }
  }

}
