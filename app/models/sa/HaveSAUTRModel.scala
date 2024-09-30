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

import play.api.libs.json.{Format, Json, OWrites, Reads}

case class HaveSAUTRModel (value : Boolean, sautrValue : Option[String])

object HaveSAUTRModel {

  def formApply(value1: Boolean, sautrValue1: Option[String]): HaveSAUTRModel = {
    if (value1) {
    sautrValue1.get.replace(" ", "") match {
      case s if s.length == 13 => HaveSAUTRModel(value1,Option(s.takeRight(10)))
      case s => HaveSAUTRModel(value1, Option(s))
    }
  } else {
      HaveSAUTRModel(value1, sautrValue1)
    }
}


  def formUnapply(arg: HaveSAUTRModel): Option[(Boolean, Option[String])] = Some((
    arg.value,
    arg.sautrValue
  ))

  implicit val fmt: Format[HaveSAUTRModel] = Json.format[HaveSAUTRModel]

}