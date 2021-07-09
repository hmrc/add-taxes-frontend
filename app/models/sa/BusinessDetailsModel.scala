/*
 * Copyright 2021 HM Revenue & Customs
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

case class BusinessDetailsModel(safeId: Option[String] = None,
                                nino: Option[String] = None,
                                mtdbsa: Option[String] = None)


object BusinessDetailsModel {

  implicit val writer: OWrites[BusinessDetailsModel] = Json.writes[BusinessDetailsModel]

  implicit val reads: Reads[BusinessDetailsModel] =
    for {
      safeId               <- (__ \ "safeId").readNullable[String]
      nino                 <- (__ \ "nino").readNullable[String]
      mtdbsa               <- (__ \ "mtdbsa").readNullable[String]
    } yield BusinessDetailsModel(
      safeId,
      nino,
      mtdbsa
    )
}