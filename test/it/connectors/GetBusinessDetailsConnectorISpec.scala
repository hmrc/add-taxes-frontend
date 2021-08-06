package connectors

import models.BusinessDetails
import org.scalatest.{MustMatchers, WordSpec}
import play.api.test.Helpers._
import support.AddTaxesIntegrationTest
import support.stubs.StubGetBusinessDetailsConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GetBusinessDetailsConnectorISpec extends WordSpec with MustMatchers with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val connector: GetBusinessDetailsConnector = inject[GetBusinessDetailsConnector]

  val identifier: String = "nino"

  val nino: String = "XX111111X"
  val mtdbsa: String = "123456789012345"

  val mtdSubscribedResponse: String =
    s"""|{
        |  "safeId": "XE00001234567890",
        |  "nino": "$nino",
        |  "mtdbsa": "$mtdbsa"
        |}""".stripMargin

  val mtdNotSubscribedResponse: String =
    """|{ "code": "NOT_FOUND_NINO",
       |  "reason": "The remote endpoint has indicated that no data can be found for the given NINO"
       |}""".stripMargin

  val internalServerErrorResponse: String =
    """|{ "code": "SERVER_ERROR",
       |  "reason": "DES is currently experiencing problems that require live service intervention"
       |}""".stripMargin

  "GetBusinessDetailsConnector" when {

    "retrieving details for a user who has been subscribed for MTD IT" should {

      "return the user's business details" in {

        StubGetBusinessDetailsConnector.withResponseGetBusinessDetails(nino)(OK, Some(mtdSubscribedResponse))

        val result: Future[Option[BusinessDetails]] = connector.getBusinessDetails(identifier, nino)

        await(result) mustBe Some(BusinessDetails(nino, mtdbsa))
      }
    }

    "retrieving details for a user who has not been subscribed for MTD IT" should {

      "return nothing" in {

        StubGetBusinessDetailsConnector.withResponseGetBusinessDetails(nino)(NOT_FOUND, Some(mtdNotSubscribedResponse))

        val result: Future[Option[BusinessDetails]] = connector.getBusinessDetails(identifier, nino)

        await(result) mustBe None
      }
    }

    "on receiving an error from the downstream system" should {

      "return nothing" in {

        StubGetBusinessDetailsConnector.withResponseGetBusinessDetails(nino)(INTERNAL_SERVER_ERROR, Some(internalServerErrorResponse))

        val result: Future[Option[BusinessDetails]] = connector.getBusinessDetails(identifier, nino)

        await(result) mustBe None
      }
    }
  }

}
