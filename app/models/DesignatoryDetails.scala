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

package models

import play.api.libs.json.{Format, Json, Reads, __}

case class Identifier(identifier: String, value: String)

case class DesignatoryDetails(firstName: String, lastName: String, nino: String, dateOfBirth: String)

object DesignatoryDetails {

  implicit val formats = {
    val reads: Reads[DesignatoryDetails] = {

      def formatDate(dob: String): String = {
        val day = dob.substring(0, 2)
        val month = dob.substring(2, 4)
        val year = dob.substring(4)
        s"$year-$month-$day"
      }

      for {
        firstName <- (__ \\ "name" \ "current" \ "firstName").read[String]
        lastName <- (__ \\ "name" \ "current" \ "lastName").read[String]
        nino <- (__ \\ "ids" \ "nino").read[String]
        dateOfBirth <- (__ \\ "dateOfBirth").read[String]
      } yield {
        DesignatoryDetails(firstName, lastName, nino, formatDate(dateOfBirth))
      }
    }

    val writes = Json.writes[DesignatoryDetails]
    Format(reads, writes)
  }
}


case class DesignatoryDetailsForKnownFacts(nino: String)

object DesignatoryDetailsForKnownFacts {
  implicit val formats = {
    val reads: Reads[DesignatoryDetailsForKnownFacts] = {
      for {
        nino <- (__ \\ "ids" \ "nino").read[String]
      } yield {
        DesignatoryDetailsForKnownFacts(nino)
      }
    }

    val writes = Json.writes[DesignatoryDetailsForKnownFacts]
    Format(reads, writes)
  }
}
