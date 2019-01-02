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

package utils.nextpage.employer.pension

import config.FrontendAppConfig
import controllers.employer.pension.routes.RequestActivationTokenController
import identifiers.DoYouHaveActivationTokenId
import models.employer.pension.DoYouHaveActivationToken
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait DoYouHaveActivationTokenNextPage {

  implicit val doYouHaveActivationToken: NextPage[DoYouHaveActivationTokenId.type, DoYouHaveActivationToken, Call] = {
    new NextPage[DoYouHaveActivationTokenId.type, DoYouHaveActivationToken, Call] {
      override def get(b: DoYouHaveActivationToken)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveActivationToken.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.PP))
          case DoYouHaveActivationToken.No  => RequestActivationTokenController.onPageLoad()
        }
    }
  }
}
