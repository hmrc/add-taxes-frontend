package connectors

import models.requests.{NavContent, NavLinks}
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import support.AddTaxesIntegrationTest
import support.stubs.StubServiceInfoPartialConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceInfoPartialConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val connector: ServiceInfoPartialConnector = inject[ServiceInfoPartialConnector]

  val testNavLinkJson: String =
    """
      |{
      | "home":{
      |         "en" : "Home",
      |         "cy" : "Hafan",
      |         "url": "http://localhost:9020/business-account"
      |       },
      | "account":{
      |           "en" : "Manage account",
      |           "cy" : "Rheoli'r cyfrif",
      |           "url" : "http://localhost:9020/business-account/manage-account"
      |       },
      | "messages":{
      |             "en" : "Messages",
      |             "cy" : "Negeseuon",
      |             "url" : "http://localhost:9020/business-account/messages",
      |             "alerts": 5
      |       },
      | "help":{
      |         "en" : "Help and contact",
      |         "cy" : "Cymorth a chysylltu",
      |         "url" : "http://localhost:9733/business-account/help"
      |       },
      | "forms":{
      |          "en" : "Track your forms{0}",
      |          "cy": "Gwirio cynnydd eich ffurflenni{0}",
      |          "url":"/track/bta",
      |          "alerts": 0
      |       }
      | }""".stripMargin


  "ServiceInfoPartialConnector" when {

    "Requesting NavLinks Content" should {
      "Return the correct json for Navlinks" in {

        val expectedNavlinks = Some(NavContent(
            home = NavLinks("Home", "Hafan", "http://localhost:9020/business-account"),
            account = NavLinks("Manage account", "Rheoli'r cyfrif", "http://localhost:9020/business-account/manage-account"),
            messages = NavLinks("Messages", "Negeseuon", "http://localhost:9020/business-account/messages", Some(5)),
            help = NavLinks("Help and contact", "Cymorth a chysylltu", "http://localhost:9733/business-account/help"),
            forms = NavLinks("Track your forms{0}", "Gwirio cynnydd eich ffurflenni{0}", "/track/bta", Some(0))))

        StubServiceInfoPartialConnector.withResponseForNavLinks()(200, Some(testNavLinkJson))

        val result: Future[Option[NavContent]] = connector.getNavLinks()

        await(result) mustBe expectedNavlinks

        StubServiceInfoPartialConnector.verifyNavlinksContent(1)

      }

      "Return None with failed status" in {

        StubServiceInfoPartialConnector.withResponseForNavLinks()(500, None)

        val result: Future[Option[NavContent]] = connector.getNavLinks()

        await(result) mustBe None

        StubServiceInfoPartialConnector.verifyNavlinksContent(1)

      }
    }
  }
}
