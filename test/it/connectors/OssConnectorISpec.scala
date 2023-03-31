package connectors

import config.FrontendAppConfig
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.vat.{OssRecievedDetails, OssRequestDetails}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import support.AddTaxesIntegrationTest
import support.stubs.StubOssConnector
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OssConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: OssConnector = inject[OssConnector]
  val testUtr = "1234567890"

  val enrolActivate: String = "enrolAndActivate"

  val origin = "BTA"
  val returnUrl = "/business-account"
  val testOssRequestDetails: String = Json.toJson(OssRequestDetails(origin, returnUrl)).toString()
  val testOssRecievedDetailsJson: String =
    s"""
       |{
       | "redirectUrl": "/test-url"
       |}
       """.stripMargin

  implicit val appConfig: FrontendAppConfig = inject[FrontendAppConfig]

  "OssConnector" when {

    "ossRegistrationJourneyLink" should {
      val userId: String = "00000000123166122235"
      val groupId: String = "ABCEDEFGI1234568"
      val confidenceLevel = ConfidenceLevel.L50

      implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, userId, confidenceLevel, None),
        HtmlFormat.empty
      )

      "return a url when successful response is returned" in {
        StubOssConnector.successFulOssRegistrationLink(testOssRequestDetails, testOssRecievedDetailsJson)

        val result: Future[OssRecievedDetails] = connector.ossRegistrationJourneyLink()

        await(result) mustBe OssRecievedDetails(Some("/test-url"))
        StubOssConnector.verifyOssRegistrationLink(1)
      }

      "return a None when the call fails" in {
        StubOssConnector.conflictOssRegistrationLink(testOssRequestDetails)

        val result: Future[OssRecievedDetails] = connector.ossRegistrationJourneyLink()

        await(result) mustBe OssRecievedDetails(None)
        StubOssConnector.verifyOssRegistrationLink(1)
      }
    }
  }
}
