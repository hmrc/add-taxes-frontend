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

package models.employer.paye

import utils.{Enumerable, RadioOption, WithName}

sealed trait DoesBusinessHaveDirectorsOrPartners

object DoesBusinessHaveDirectorsOrPartners {

  case object Director extends WithName("Director") with DoesBusinessHaveDirectorsOrPartners
  case object Partner extends WithName("Partner") with DoesBusinessHaveDirectorsOrPartners
  case object Solo extends WithName("Solo") with DoesBusinessHaveDirectorsOrPartners

  val values: Set[DoesBusinessHaveDirectorsOrPartners] = Set(
    Director,
    Partner,
    Solo
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("doesBusinessHaveDirectorsOrPartners", value.toString)
  }

  implicit val enumerable: Enumerable[DoesBusinessHaveDirectorsOrPartners] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
