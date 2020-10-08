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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, stubFor, urlPathEqualTo}
import models.requests.AuthenticatedRequest
import play.api.libs.json._
import uk.gov.hmrc.auth.core._

object StubAuth {

  def stubAuthorisedUserSa(authProfile: AuthenticatedRequest[_]): Unit = {

    val responseJson: JsValue =
      Json.obj(
        "externalId" -> authProfile.externalId,
        "allEnrolments" -> Json.obj("key" -> "IR-SA", "value" -> "1234567890"),
        "affinityGroup" -> authProfile.affinityGroup.toString,
        "groupIdentifier" -> authProfile.groupId,
        "optionalCredentials" ->
        (authProfile.credId match {
          case credId =>
        Json.obj(
          "providerId" -> credId,
          "providerType" -> "GovernmmentGateway"
        )
          case _ => Json.obj()
        }),
        "confidenceLevel" -> (authProfile.confidenceLevel match {
          case ConfidenceLevel.L200 => 200
          case ConfidenceLevel.L100 => 100
          case ConfidenceLevel.L50 => 50
        })
      )

    stubFor(
      post(urlPathEqualTo(s"/auth/authorise")).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(responseJson.toString())
      )
    )
  }

}
