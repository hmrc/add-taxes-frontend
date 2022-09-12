package connectors

import models.DesignatoryDetails
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import support.AddTaxesIntegrationTest
import support.stubs.StubCitizenDetailsConnector
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class CitizenDetailsConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  val connector: CitizensDetailsConnector = inject[CitizensDetailsConnector]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val identifier: String = "IR-SA"

  val firstName: String = "John"
  val surname: String = "Smith"
  val nino: String = "AA055075C"
  val dateOfBirthShortFormat: String = "11121971"
  val dateOfBirthLongFormat: String = "1971-12-11"

  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  val designatoryDetails: String =  s"""
                                      |{
                                      |  "name": {
                                      |    "current": {
                                      |      "firstName": "$firstName",
                                      |      "lastName": "$surname"
                                      |    },
                                      |    "previous": []
                                      |  },
                                      |  "ids": {
                                      |    "nino": "$nino"
                                      |  },
                                      |  "dateOfBirth": "$dateOfBirthShortFormat"
                                      |}
                                      |""".stripMargin

  val invalidResponse: String = s"""
                                   |{
                                   |  "error": "[RATE LIMITED] status: 503"
                                   |}
                                   |""".stripMargin

  "Citizen details connector" when {

    "getDesignatoryDetails is called" should {

      "return None if designatory details Not Found" in {
        StubCitizenDetailsConnector.stubDesignatoryDetailsNotFound("1234567890")
        await(connector.getDesignatoryDetails(identifier, "1234567890")) mustBe None
      }

      "return a DesignatoryDetails object if call was successful" in {
        StubCitizenDetailsConnector.withResponseForGetDesignatoryDetails("1234567890")(OK, Some(designatoryDetails))

        val result: Future[Option[DesignatoryDetails]] = connector.getDesignatoryDetails(identifier, "1234567890")

        await(result) mustBe Some(DesignatoryDetails(firstName, surname, nino, dateOfBirthLongFormat))
      }

      "return a None if the Data cannot be parsed" in {
        StubCitizenDetailsConnector.withResponseForGetDesignatoryDetails("1234567890")(INTERNAL_SERVER_ERROR, Some(invalidResponse))

          await(connector.getDesignatoryDetails(identifier, "1234567890")) mustBe None
      }

    }

  }

}
