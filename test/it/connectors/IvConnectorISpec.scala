/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import models.sa.JourneyLinkResponse
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import support.AddTaxesIntegrationTest
import support.stubs.StubIvConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IvConnectorISpec extends PlaySpec with AddTaxesIntegrationTest {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val connector: IvConnector = inject[IvConnector]

  val checkJourneyLinkJson: Option[String] = Some(
    s"""
       |{
       | "result" : "Success",
       | "token" : "token"
       |}
       """.stripMargin)

  "IvConnector" when {
    "checkJourneyLink" should {
      "return a valid JourneyLinkResponse" in {
        StubIvConnector.withResponseCheckJourneyLink()(OK, checkJourneyLinkJson)

        val result: Future[JourneyLinkResponse] = connector.checkJourneyLink("/mdtp/journey/journeyId/iv-stub-data")

        await(result) mustBe JourneyLinkResponse("Success", "token")
        StubIvConnector.verifyCheckJourneyLink(1)
      }
    }

    "checkJourneyLinkUplift" should {
      "return a valid JourneyLinkResponse" in {
        StubIvConnector.withResponseCheckJourneyLinkUplift()(OK, checkJourneyLinkJson, "12345")

        val result: Future[JourneyLinkResponse] = connector.checkJourneyLinkUplift("12345")

        await(result) mustBe JourneyLinkResponse("Success", "token")
        StubIvConnector.verifyCheckJourneyLinkUplift(1, "12345")
      }
    }
  }
}
