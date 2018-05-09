/*
 * Copyright 2018 HM Revenue & Customs
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

package models.other.importexports

import utils.{Enumerable, RadioOption, WithName}

sealed trait DoYouWantToAddImportExport

object DoYouWantToAddImportExport {

  case object EMCS extends WithName("EMCS") with DoYouWantToAddImportExport
  case object ICS extends WithName("ICS") with DoYouWantToAddImportExport
  case object DDES extends WithName("DDES") with DoYouWantToAddImportExport
  case object NOVA extends WithName("NOVA") with DoYouWantToAddImportExport
  case object NCTS extends WithName("NCTS") with DoYouWantToAddImportExport
  case object eBTI extends WithName("eBTI") with DoYouWantToAddImportExport
  case object NES extends WithName("NES") with DoYouWantToAddImportExport
  case object ISD extends WithName("ISD") with DoYouWantToAddImportExport

  val values: List[DoYouWantToAddImportExport] = List(
    EMCS, ICS, DDES, NOVA, NCTS, eBTI, NES, ISD
  )

  val options: List[RadioOption] = values.map {
    value =>
      RadioOption("doYouWantToAddImportExport", value.toString)
  }

  implicit val enumerable: Enumerable[DoYouWantToAddImportExport] =
    Enumerable(values.map(v => v.toString -> v): _*)
}