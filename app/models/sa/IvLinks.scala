/*
 * Copyright 2023 HM Revenue & Customs
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

package models.sa

import play.api.libs.json.{Json, OFormat}

case class IvLinks(link: String, journeyLink: String)

object IvLinks {
  implicit val format: OFormat[IvLinks] = Json.format[IvLinks]
}

case class JourneyLinkResponse(result: String, token: String)

object JourneyLinkResponse {
  implicit val format: OFormat[JourneyLinkResponse] = Json.format[JourneyLinkResponse]
}
