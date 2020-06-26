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

package utils.nextpage.other.gambling

import config.FrontendAppConfig
import controllers.other.gambling.gbd.{routes => gbdRoutes}
import identifiers.AreYouRegisteredGTSId
import models.other.gambling.gbd.AreYouRegisteredGTS
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}

trait AreYouRegisteredGTSNextPage {

  implicit val gbdGTS: NextPage[AreYouRegisteredGTSId.GBD.type, AreYouRegisteredGTS, Call] = {
    new NextPage[AreYouRegisteredGTSId.GBD.type, AreYouRegisteredGTS, Call] {
      override def get(b: AreYouRegisteredGTS)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.GeneralBetting))
          case AreYouRegisteredGTS.No  => gbdRoutes.RegisterGBDController.onPageLoad()
        }
    }
  }
}
