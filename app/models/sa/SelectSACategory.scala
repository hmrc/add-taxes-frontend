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

import utils.{Enumerable, RadioOption, WithName}

sealed trait SelectSACategory

object SelectSACategory {

  case object Sa extends WithName("Sa") with SelectSACategory
  case object Partnership extends WithName("Partnership") with SelectSACategory
  case object Trust extends WithName("Trust") with SelectSACategory

  val values: Set[SelectSACategory] = Set(
    Sa,
    Partnership,
    Trust
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("selectSACategory", value.toString)
  }

  implicit val enumerable: Enumerable[SelectSACategory] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
