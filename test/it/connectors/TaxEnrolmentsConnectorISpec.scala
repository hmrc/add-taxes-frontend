package connectors

import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.SaEnrolment
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import support.AddTaxesIntegrationTest
import support.stubs.StubTaxEnrolmentsConnector
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import utils.HmrcEnrolmentType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaxEnrolmentsConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: TaxEnrolmentsConnector = inject[TaxEnrolmentsConnector]
  val testUtr = "1234567890"

  val enrolActivate: String = "enrolAndActivate"

  "TaxEnrolmentsConnector" when {

    "enrolForSa" should {
      val userId: String = "00000000123166122235"
      val groupId: String = "ABCEDEFGI1234568"
      val saEnrolment = new SaEnrolment(userId, "enrolAndActivate")

      val confidenceLevel = ConfidenceLevel.L50

      implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, userId, confidenceLevel, None),
        HtmlFormat.empty
      )

      "return a true when a enrolment is created" in {
        StubTaxEnrolmentsConnector.successFulEnrolForSa(saEnrolment, testUtr, groupId)

        val result: Future[Boolean] = connector.enrolForSa(testUtr, enrolActivate)

        await(result) mustBe true
        StubTaxEnrolmentsConnector.verifyEnrolForSa(1, groupId, testUtr)
      }

      "return a false when an enrolment is not created" in {
        StubTaxEnrolmentsConnector.unsuccessFulEnrolForSa(saEnrolment, testUtr, groupId)

        val result: Future[Boolean] = connector.enrolForSa(testUtr, enrolActivate)

        await(result) mustBe false
        StubTaxEnrolmentsConnector.verifyEnrolForSa(1, groupId, testUtr)
      }

      "return a true when conflict is returned but the user already has the IR-SA enrolment" in {
        StubTaxEnrolmentsConnector.conflictEnrolForSa(saEnrolment, testUtr, groupId)

        val saAuthEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(), state = "Activated")

        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set(saAuthEnrolment)), Some(Individual), groupId, userId, confidenceLevel, None),
          HtmlFormat.empty
        )

        val result: Future[Boolean] = connector.enrolForSa(testUtr, enrolActivate)

        await(result) mustBe true
        StubTaxEnrolmentsConnector.verifyEnrolForSa(1, groupId, testUtr)
      }

      "return a false when conflict is returned and the user does not have the IR-SA enrolment" in {
        StubTaxEnrolmentsConnector.conflictEnrolForSa(saEnrolment, testUtr, groupId)

        implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, userId, confidenceLevel, None),
          HtmlFormat.empty
        )

        val result: Future[Boolean] = connector.enrolForSa(testUtr, enrolActivate)

        await(result) mustBe false
        StubTaxEnrolmentsConnector.verifyEnrolForSa(1, groupId, testUtr)
      }
    }
  }
}
