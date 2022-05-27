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

package utils.nextpage.other.gambling

import config.FrontendAppConfig
import identifiers.SelectGamblingOrGamingDutyId
import play.api.mvc.{Call, Request}
import utils.NextPage
import controllers.other.gambling.mgd.{routes => mgdRoutes}
import controllers.other.gambling.gbd.{routes => gbdRoutes}
import controllers.other.gambling.pbd.{routes => pbdRoutes}
import controllers.other.gambling.rgd.{routes => rgdRoutes}
import models.other.gambling.SelectGamblingOrGamingDuty

trait SelectGamblingOrGamingDutyNextPage {

  implicit val selectGamblingOrGamingDuty
    : NextPage[SelectGamblingOrGamingDutyId.type, SelectGamblingOrGamingDuty, Call] = {
    new NextPage[SelectGamblingOrGamingDutyId.type, SelectGamblingOrGamingDuty, Call] {
      override def get(b: models.other.gambling.SelectGamblingOrGamingDuty)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case SelectGamblingOrGamingDuty.MGD =>
            mgdRoutes.DoYouHaveMGDRegistrationController.onPageLoad()
          case SelectGamblingOrGamingDuty.GBD =>
            gbdRoutes.AreYouRegisteredGTSController.onPageLoad()
          case SelectGamblingOrGamingDuty.PBD =>
            pbdRoutes.DoYouHavePBDRegistrationController.onPageLoad()
          case SelectGamblingOrGamingDuty.RGD =>
            rgdRoutes.DoYouHaveRGDRegistrationController.onPageLoad()
        }
    }
  }
}
