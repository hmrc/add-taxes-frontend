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

package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object StubIvConnector extends StubHelper {

  def withResponseCheckJourneyLink()(status: Int, optBody: Option[String]): Unit =
    stubGet("/mdtp/journey/journeyId/iv-stub-data", status, optBody)

  def withResponseCheckJourneyLinkUplift()(status: Int, optBody: Option[String], journeyId: String): Unit =
    stubGet(s"/iv-stub/mdtp/journey/journeyId/$journeyId", status, optBody)

  def verifyCheckJourneyLink(count: Int): Unit =
    verify(count, getRequestedFor(urlMatching(s"/mdtp/journey/journeyId/iv-stub-data")))


  def verifyCheckJourneyLinkUplift(count: Int, journeyId: String): Unit =
    verify(count, getRequestedFor(urlMatching(s"/iv-stub/mdtp/journey/journeyId/$journeyId")))


  def withResponseSsttpJourney(status: Int): Unit =
    stubPostEmpty("/internal/enrolled-for-sa", status)

  def verifySsttpJourney(count: Int): Unit =
    verify(count, postRequestedFor(urlMatching(s"/internal/enrolled-for-sa")))

}
