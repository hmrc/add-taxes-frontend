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

package models.sa

import play.api.libs.json._

import scala.collection.immutable

class EnrolmentStoreProxyModel

case class KnownFacts(postcode: Option[String], nino: Option[String], isAbroad: Option[String]) {
  def provided: Seq[String] = List(postcode, nino, isAbroad).flatten
  def isValid: Boolean = this.provided.nonEmpty
  def validSize: Boolean = this.provided.size == 1
}

object KnownFacts {
  implicit val format: OFormat[KnownFacts] = Json.format[KnownFacts]
}

case class KnownFactsAndIdentifiers(utr: String, nino: Option[String], postcode: Option[String], isAbroad: Option[String])

object KnownFactsAndIdentifiers {
  implicit val reads: Reads[KnownFactsAndIdentifiers] = Json.reads[KnownFactsAndIdentifiers]

  implicit val writes: Writes[KnownFactsAndIdentifiers] = (kif: KnownFactsAndIdentifiers) => {
    Json.obj("service" -> "IR-SA") ++
    Json.obj("knownFacts" ->
      Json.toJson(
        List(
          Some(Json.obj("key" -> "UTR", "value" -> kif.utr)),
          kif.nino.map(x => Json.obj("key" -> "NINO", "value" -> x)),
          kif.postcode.map(x => Json.obj("key" -> "Postcode", "value" -> x)),
          kif.isAbroad.map(x => Json.obj("key" -> "IsAbroad", "value" -> x))
        ).flatten
      )
    )
  }
}

case class SaEnrolment(userId: String, action: String)

object SaEnrolment {

  implicit val reads: Reads[SaEnrolment] = Json.reads[SaEnrolment]

  implicit val writes: Writes[SaEnrolment] = (saEnrolment: SaEnrolment) => {
    Json.obj("userId" -> saEnrolment.userId,
      "type" -> "principal",
      "action" -> saEnrolment.action)
  }
}

case class KnownFactsReturn(utr: String, knownFactsResult: Boolean)

