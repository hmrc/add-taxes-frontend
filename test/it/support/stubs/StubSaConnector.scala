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
import play.api.test.Helpers.{OK, NOT_FOUND}

object StubSaConnector extends StubHelper {

  def withResponseForGetIvLinks(utr: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(s"/sa/individual/${utr}/details-for-iv", status, optBody)

  def successfulLinkRetrieval(utr: String) = withResponseForGetIvLinks(utr)(OK,
  Some(
    s"""
       |{
       |  "link": "/mdtp/confirm/proxy/some-iv-link",
       |  "journeyLink": "/mdtp/journey/journeyId/iv-stub-data"
       |}""".stripMargin
  ))

  def unSuccessfulLinkRetrieval(utr: String) = withResponseForGetIvLinks(utr)(NOT_FOUND,
    None)

  def verifyGetIvLinks(count: Int, utr: String): Unit =
    verify(count, getRequestedFor(urlMatching(s"/sa/individual/$utr/details-for-iv")))

}
