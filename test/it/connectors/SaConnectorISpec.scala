package connectors

import models.sa.IvLinks
import org.scalatest.{MustMatchers, WordSpec}
import play.api.test.Helpers.{await, _}
import support.AddTaxesIntegrationTest
import support.stubs.StubSaConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SaConnectorISpec extends WordSpec with MustMatchers with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: SaConnector = inject[SaConnector]
  val testUtr = "1234567890"
  val origin = "bta-sa"

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
  }

}
