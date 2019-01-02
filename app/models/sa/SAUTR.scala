/*
 * Copyright 2019 HM Revenue & Customs
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

class SAUTR private (saUTR: String) {
  val value: String = saUTR
}

object SAUTR {

  val utrLength = 10

  def apply(value: String): SAUTR =
    value.replace(" ", "") match {
      case s if s.length == 13 => new SAUTR(s.takeRight(utrLength))
      case s                   => new SAUTR(s)
    }

  def unapply(saUTR: SAUTR): Option[String] = Some(saUTR.value)
}
