package connectors

import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{KnownFacts, KnownFactsReturn, SAUTR}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import support.AddTaxesIntegrationTest
import support.stubs.StubEnrolmentStoreConnector
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreProxyConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: EnrolmentStoreProxyConnector = inject[EnrolmentStoreProxyConnector]
  val testUtr = "1234567890"
  val credId: String = "cred"
  override val groupId: String = "HSHGFG734-YHDJS83"
  val enrolActivate: String = "enrolAndActivate"
  val testTaxOfficeNumber = "123"
  val testTaxOfficeReference = "4567890"
  val testAllKnownFacts: KnownFacts = KnownFacts(Some("AA1 1AA"), Some("AA00000A"), None)
  val testNinoOnlyKnownFacts: KnownFacts = KnownFacts(None, Some("AA00000A"), None)
  val testPostCodeOnlyKnownFacts: KnownFacts = KnownFacts(Some("AA1 1AA"), None, None)

  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  "EnrolmentStoreProxyConnector" when {
    "checkExistingUTR" should {
      "return false when NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.checkUtrNoContentResponse(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr, "IR-SA")

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
      }

      "return false when a status other than OK and NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.unsuccessfulCheckUtrResponse(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr, "IR-SA")

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
      }

      "return true when a status OK  is received and principal enrolments found" in {
        StubEnrolmentStoreConnector.successfulCheckUtrOkResponsePrincipal(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr, "IR-SA")

        await(result) mustBe true
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
      }

      "return false when a status OK  is received and no principal enrolments found" in {
        StubEnrolmentStoreConnector.successfulCheckUtrOkResponseDelegated(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr, "IR-SA")

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
      }
    }

    "checkSaGroup" should {
      "return false when NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.checkGroupSAResponseNoContent(groupId)

        val result: Future[Boolean] = connector.checkSaGroup(groupId, "IR-SA")

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckGroupSA(1, groupId)
      }

      "return false when a status other than OK and NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.checkGroupSAResponseInternalServerError(groupId)

        val result: Future[Boolean] = connector.checkSaGroup(groupId, "IR-SA")

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckGroupSA(1, groupId)
      }

      "return true when a status OK  is received and json contains IR-SA" in {
        StubEnrolmentStoreConnector.checkGroupSAResponseOK(groupId, StubEnrolmentStoreConnector.es3ResponseEnrolmentsWithSA)

        val result: Future[Boolean] = connector.checkSaGroup(groupId, "IR-SA")

        await(result) mustBe true
        StubEnrolmentStoreConnector.verifyCheckGroupSA(1, groupId)
      }

      "return false when a status OK  is received and Json does not contain IR-SA" in {
        StubEnrolmentStoreConnector.checkGroupSAResponseOK(groupId, StubEnrolmentStoreConnector.es3ResponseEnrolmentsWithOutSa)

        val result: Future[Boolean] = connector.checkSaGroup(groupId, "IR-SA")

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckGroupSA(1, groupId)
      }
    }

    "checkExistingEmpRef" should {
      "return false when NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.unsuccessfulExistingEmpRefNotFoundResponse(testTaxOfficeNumber, testTaxOfficeReference)

        val result: Future[Boolean] = connector.checkExistingEmpRef(testTaxOfficeNumber, testTaxOfficeReference)

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyExistingEmpRef(1, testTaxOfficeNumber, testTaxOfficeReference)
      }
      "return false when a status other than OK and NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.unsuccessfulExistingEmpRefResponse(testTaxOfficeNumber, testTaxOfficeReference)

        val result: Future[Boolean] = connector.checkExistingEmpRef(testTaxOfficeNumber, testTaxOfficeReference)

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyExistingEmpRef(1, testTaxOfficeNumber, testTaxOfficeReference)
      }
      "return true when a status OK  is received" in {
        StubEnrolmentStoreConnector.successfulExistingEmpRefOkResponse(testTaxOfficeNumber, testTaxOfficeReference)

        val result: Future[Boolean] = connector.checkExistingEmpRef(testTaxOfficeNumber, testTaxOfficeReference)

        await(result) mustBe true
        StubEnrolmentStoreConnector.verifyExistingEmpRef(1, testTaxOfficeNumber, testTaxOfficeReference)
      }
    }

    "queryKnownFacts" should {
      "return KnownFactsReturn with a result of false when NO_CONTENT is received" in {
        val postBody: String = StubEnrolmentStoreConnector.queryKnownFactsAllDataPost(testUtr, testAllKnownFacts)
        StubEnrolmentStoreConnector.notMatchedQueryKnownFacts(postBody)

        val result: Future[KnownFactsReturn] = connector.queryKnownFacts(SAUTR(testUtr), testAllKnownFacts)

        await(result) mustBe KnownFactsReturn(testUtr, knownFactsResult = false)
        StubEnrolmentStoreConnector.verifyQueryKnownFacts(1)
      }

      "return KnownFactsReturn when all data is submitted and has a result of true when a OK is received" in {
        val postBody: String = StubEnrolmentStoreConnector.queryKnownFactsAllDataPost(testUtr, testAllKnownFacts)
        StubEnrolmentStoreConnector.successfulQueryKnownFacts(postBody)

        val result: Future[KnownFactsReturn] = connector.queryKnownFacts(SAUTR(testUtr), testAllKnownFacts)

        await(result) mustBe KnownFactsReturn(testUtr, knownFactsResult = true)
        StubEnrolmentStoreConnector.verifyQueryKnownFacts(1)
      }

      "return KnownFactsReturn when Nino Only is submitted and has a result of true when a OK is received" in {
        val postBody: String = StubEnrolmentStoreConnector.queryKnownFactsOnlyNinoPost(testUtr, testNinoOnlyKnownFacts)
        StubEnrolmentStoreConnector.successfulQueryKnownFacts(postBody)

        val result: Future[KnownFactsReturn] = connector.queryKnownFacts(SAUTR(testUtr), testNinoOnlyKnownFacts)

        await(result) mustBe KnownFactsReturn(testUtr, knownFactsResult = true)
        StubEnrolmentStoreConnector.verifyQueryKnownFacts(1)
      }

      "return KnownFactsReturn when Postcode Only is submitted and has a result of true when a OK is received" in {
        val postBody: String = StubEnrolmentStoreConnector.queryKnownFactsOnlyPostCodePost(testUtr, testPostCodeOnlyKnownFacts)
        StubEnrolmentStoreConnector.successfulQueryKnownFacts(postBody)

        val result: Future[KnownFactsReturn] = connector.queryKnownFacts(SAUTR(testUtr), testPostCodeOnlyKnownFacts)

        await(result) mustBe KnownFactsReturn(testUtr, knownFactsResult = true)
        StubEnrolmentStoreConnector.verifyQueryKnownFacts(1)
      }
    }

  }
}
