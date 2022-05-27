/*
 * Copyright 2022 HM Revenue & Customs
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

package models.vat

import play.api.libs.json.{Json, OFormat}


case class OssRequestDetails(origin: String, returnUrl: String)

object OssRequestDetails {
  implicit val format: OFormat[OssRequestDetails] = Json.format[OssRequestDetails]
}

case class OssRecievedDetails(redirectUrl: Option[String])

object OssRecievedDetails {
  implicit val format: OFormat[OssRecievedDetails] = Json.format[OssRecievedDetails]
}
