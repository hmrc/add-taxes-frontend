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

package utils.nextpage.employer.paye

import config.FrontendAppConfig
import identifiers.EnterPAYEReferenceId
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}

trait EnterPAYEReferenceNextPage {

  type EmpRefExists = Boolean

  implicit val enterPAYEReference: NextPage[EnterPAYEReferenceId.type, EmpRefExists, Call] = {

    new NextPage[EnterPAYEReferenceId.type, EmpRefExists, Call] {
      override def get(b: EmpRefExists)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case false => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.EPAYE))
          case true  => Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials"))
        }
    }

  }

}
