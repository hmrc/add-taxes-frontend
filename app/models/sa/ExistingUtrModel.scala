/*
 * Copyright 2024 HM Revenue & Customs
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

case class ExistingUtrModel(principalUserIds: Seq[String])

object ExistingUtrModel {
  implicit val format: OFormat[ExistingUtrModel] = Json.format[ExistingUtrModel]
}

case class QueryGroupsEnrolmentsResponseModel(enrolments: Seq[Enrolment])

case class Enrolment(service: String)

object QueryGroupsEnrolmentsResponseModel {
  implicit val enrolmentReads: OFormat[Enrolment] = Json.format[Enrolment]
  implicit val queryGroupsEnrolmentsReads: OFormat[QueryGroupsEnrolmentsResponseModel] = Json.format[QueryGroupsEnrolmentsResponseModel]
}
