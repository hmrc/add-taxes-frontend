package connectors

import config.FrontendAppConfig
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.vat.{VatOneStopRecievedDetails, VatOneStopRequestDetails, WhichVATServicesToAdd}
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
  lazy val connector: VatOneStopConnector = inject[VatOneStopConnector]
  val testUtr = "1234567890"

  val enrolActivate: String = "enrolAndActivate"

  val origin = "BTA"
  val returnUrl = "/business-account"
  val testOneStopRequestDetails: String = Json.toJson(VatOneStopRequestDetails(origin, returnUrl)).toString()
  val testOneStopRecievedDetailsJson: String =
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

      "return a url when successful response is returned with VAT OSS" in {
        val lang: String = "en"
        StubOssConnector.successFulOneStopRegistrationLink(testOneStopRequestDetails, testOneStopRecievedDetailsJson, iossBool = false, lang)

        val result: Future[VatOneStopRecievedDetails] = connector.vatOneStopRegistrationJourneyLink(WhichVATServicesToAdd.VATOSS, lang)

        await(result) mustBe VatOneStopRecievedDetails(Some("/test-url"))
        StubOssConnector.verifyOneStopRegistrationLink(count = 1, iossBool = false,lang = lang)
      }

      "return a url when successful response is returned with VAT IOSS and english lang is passed" in {
        val lang: String = "en"
        StubOssConnector.successFulOneStopRegistrationLink(testOneStopRequestDetails, testOneStopRecievedDetailsJson, iossBool = true, lang)

        val result: Future[VatOneStopRecievedDetails] = connector.vatOneStopRegistrationJourneyLink(WhichVATServicesToAdd.VATIOSS, lang)

        await(result) mustBe VatOneStopRecievedDetails(Some("/test-url"))
        StubOssConnector.verifyOneStopRegistrationLink(count = 1, iossBool = true, lang = lang)
      }

      "return a url when successful response is returned with VAT IOSS and CY lang is passed" in {
        val lang: String = "cy"
        StubOssConnector.successFulOneStopRegistrationLink(testOneStopRequestDetails, testOneStopRecievedDetailsJson, iossBool = true, lang)

        val result: Future[VatOneStopRecievedDetails] = connector.vatOneStopRegistrationJourneyLink(WhichVATServicesToAdd.VATIOSS, lang)

        await(result) mustBe VatOneStopRecievedDetails(Some("/test-url"))
        StubOssConnector.verifyOneStopRegistrationLink(count = 1, iossBool = true, lang = lang)
      }

      "return a None when the call fails" in {
        val lang: String = "en"
        StubOssConnector.conflictOneStopRegistrationLink(testOneStopRequestDetails, iossBool = false, lang)

        val result: Future[VatOneStopRecievedDetails] = connector.vatOneStopRegistrationJourneyLink(WhichVATServicesToAdd.VATOSS, lang)

        await(result) mustBe VatOneStopRecievedDetails(None)
        StubOssConnector.verifyOneStopRegistrationLink(1, iossBool = false, lang = lang)
      }
    }
  }
}
