package connectors

import models.sa.{KnownFacts, KnownFactsReturn, SAUTR, SaEnrolment}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.test.Helpers._
import support.AddTaxesIntegrationTest
import support.stubs.{StubEnrolmentStoreConnector, StubTaxEnrolmentsConnector}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaxEnrolmentsConnectorISpec extends WordSpec with MustMatchers with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: TaxEnrolmentsConnector = inject[TaxEnrolmentsConnector]
  val testUtr = "1234567890"

  val enrolActivate: String = "enrolAndActivate"

  "TaxEnrolmentsConnector" when {

    "enrolForSa" should {
      val userId: String = "00000000123166122235"
      val groupId: String = "ABCEDEFGI1234568"
      val saEnrolment = new SaEnrolment(userId, "enrolAndActivate")

      "return a true when a enrolment is created" in {
        StubTaxEnrolmentsConnector.successFulEnrolForSa(saEnrolment, testUtr, groupId)

        val result: Future[Boolean] = connector.enrolForSa(testUtr, userId, groupId, enrolActivate)

        await(result) mustBe true
        StubTaxEnrolmentsConnector.verifyEnrolForSa(1, groupId, testUtr)
      }

      "return a false when an enrolment is not created" in {
        StubTaxEnrolmentsConnector.unsuccessFulEnrolForSa(saEnrolment, testUtr, groupId)

        val result: Future[Boolean] = connector.enrolForSa(testUtr, userId, groupId, enrolActivate)

        await(result) mustBe false
        StubTaxEnrolmentsConnector.verifyEnrolForSa(1, groupId, testUtr)
      }
    }
  }
}
