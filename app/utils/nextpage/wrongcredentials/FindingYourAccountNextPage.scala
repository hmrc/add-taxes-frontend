/*
 * Copyright 2022 HM Revenue & Customs
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

package utils.nextpage.wrongcredentials

import config.FrontendAppConfig
import identifiers.FindingYourAccountId
import models.wrongcredentials.FindingYourAccount
import play.api.mvc.{Call, Request}
import utils.{ForgottenOptions, NextPage}

trait FindingYourAccountNextPage {

  implicit val findingYourAccount: NextPage[FindingYourAccountId.type, FindingYourAccount, Call] = {
    new NextPage[FindingYourAccountId.type, FindingYourAccount, Call] {
      override def get(b: FindingYourAccount)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case FindingYourAccount.DontKnowPassword =>
            Call("GET", appConfig.lostCredentials(ForgottenOptions.ForgottenPassword))

          case FindingYourAccount.DontKnowId =>
            Call("GET", appConfig.lostCredentials(ForgottenOptions.ForgottenId))

          case FindingYourAccount.DontKnowIdOrPassword =>
            Call("GET", appConfig.lostCredentials(ForgottenOptions.ForgottenIdAndPassword))
        }
    }
  }
}
