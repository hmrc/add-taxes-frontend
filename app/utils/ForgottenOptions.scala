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

package utils

sealed trait ForgottenOptions

object ForgottenOptions {
  case object ForgottenPassword extends WithName("password") with ForgottenOptions
  case object ForgottenId extends WithName("userid") with ForgottenOptions
  case object ForgottenIdAndPassword extends WithName("both") with ForgottenOptions

  val values: Set[ForgottenOptions] = Set(
    ForgottenPassword,
    ForgottenId,
    ForgottenIdAndPassword
  )
}
