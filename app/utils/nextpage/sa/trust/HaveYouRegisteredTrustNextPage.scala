/*
 * Copyright 2023 HM Revenue & Customs
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

package utils.nextpage.sa.trust

import config.FrontendAppConfig
import identifiers.HaveYouRegisteredTrustId
import models.sa.trust.HaveYouRegisteredTrust
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import utils.{Enrolments, NextPage}
import controllers.sa.trust.{routes => trustRoutes}

trait HaveYouRegisteredTrustNextPage {

  implicit val haveYouRegisteredTrust: NextPage[HaveYouRegisteredTrustId.type, HaveYouRegisteredTrust, Call] = {
    new NextPage[HaveYouRegisteredTrustId.type, HaveYouRegisteredTrust, Call] {
      override def get(b: HaveYouRegisteredTrust)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case HaveYouRegisteredTrust.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.RegisterTrusts))
          case HaveYouRegisteredTrust.No  => trustRoutes.RegisterTrustController.onPageLoad()
        }
    }
  }
}
