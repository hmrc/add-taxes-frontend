package connectors

import models.sa.{KnownFacts, KnownFactsReturn, SAUTR, SaEnrolment}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.test.Helpers._
import support.AddTaxesIntegrationTest
import support.stubs.StubEnrolmentStoreConnector
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreProxyConnectorISpec extends WordSpec with MustMatchers with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  lazy val connector: EnrolmentStoreProxyConnector = inject[EnrolmentStoreProxyConnector]
  val testUtr = "1234567890"
  val testTaxOfficeNumber = "123"
  val testTaxOfficeReference = "4567890"
  val testAllKnownFacts: KnownFacts = KnownFacts(Some("AA1 1AA"), Some("AA00000A"), None)
  val testNinoOnlyKnownFacts: KnownFacts = KnownFacts(None, Some("AA00000A"), None)
  val testPostCodeOnlyKnownFacts: KnownFacts = KnownFacts(Some("AA1 1AA"), None, None)

  "EnrolmentStoreProxyConnector" when {
    "checkExistingUTR" should {
      "return false when NO_CONTENT is received" in {
        StubEnrolmentStoreConnector.unsuccessfulCheckUtrNotFoundResponse(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr)

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
      }

      "return false when a status other than OK and NO_CONTENT is received" in {
       StubEnrolmentStoreConnector.unsuccessfulCheckUtrResponse(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr)

        await(result) mustBe false
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
      }

      "return true when a status OK  is received" in {
        StubEnrolmentStoreConnector.successfulCheckUtrOkResponse(testUtr)

        val result: Future[Boolean] = connector.checkExistingUTR(testUtr)

        await(result) mustBe true
        StubEnrolmentStoreConnector.verifyCheckUtr(1, testUtr)
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

    "enrolForSa" should {
      val userId: String = "00000000123166122235"
      val groupId: String = "ABCEDEFGI1234568"
      val saEnrolment = new SaEnrolment(userId, "enrolAndActivate")

      "return a valid HttpResponse" in {
        StubEnrolmentStoreConnector.successFulEnrolForSa(saEnrolment, testUtr, groupId)

        val result: Future[HttpResponse] = connector.enrolForSa(saEnrolment, testUtr, groupId)

        await(result.map(_.status)) mustBe CREATED
        StubEnrolmentStoreConnector.verifyEnrolForSa(1, groupId, testUtr)
        }
      }
    }
}
