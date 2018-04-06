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

package utils

sealed trait Enrolments
object Enrolments {
 case object RebatedOils extends WithName("HMCE-RO") with Enrolments
 case object TiedOils extends WithName("HMCE-TO") with Enrolments
 case object AlcoholAndTobaccoWarehousingDeclarations extends WithName("HMCE-ATWD-ORG") with Enrolments
 case object AnnualTaxOnEnvelopedDwellings extends WithName("HMRC-AWRS-ORG") with Enrolments

  val values: Set[Enrolments] = Set(
    RebatedOils, TiedOils, AlcoholAndTobaccoWarehousingDeclarations, AnnualTaxOnEnvelopedDwellings
  )
}
