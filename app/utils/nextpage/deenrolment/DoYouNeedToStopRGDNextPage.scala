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

package utils.nextpage.deenrolment

import config.FrontendAppConfig
import identifiers.DoYouNeedToStopRGDId
import play.api.mvc.{Call, Request}
import models.deenrolment.DoYouNeedToStopRGD
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}

trait DoYouNeedToStopRGDNextPage {

  implicit val doYouNeedToStopRGD: NextPage[DoYouNeedToStopRGDId.type, DoYouNeedToStopRGD, Call] = {
    new NextPage[DoYouNeedToStopRGDId.type, DoYouNeedToStopRGD, Call] {
      override def get(b: DoYouNeedToStopRGD)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoYouNeedToStopRGD.Yes => Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.RemoteGaming))
          case DoYouNeedToStopRGD.No  => Call("GET", appConfig.getGovUKUrl("deenrolGambling"))
        }
    }
  }
}
