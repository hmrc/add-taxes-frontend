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

package utils.nextpage.other.gambling

import config.FrontendAppConfig
import controllers.other.gambling.gbd.{routes => gbdRoutes}
import controllers.other.gambling.pbd.{routes => pbdRoutes}
import controllers.other.gambling.rgd.{routes => rgdRoutes}
import identifiers.AreYouRegisteredGTSId
import models.other.gambling.gbd.AreYouRegisteredGTS
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait AreYouRegisteredGTSNextPage {

  implicit val rgdGTS: NextPage[AreYouRegisteredGTSId.RGD.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.RGD.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.RemoteGaming))
          case AreYouRegisteredGTS.No  => rgdRoutes.RegisterRGDController.onPageLoad()
        }
    }
  }

  implicit val gbdGTS: NextPage[AreYouRegisteredGTSId.GBD.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.GBD.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.GeneralBetting))
          case AreYouRegisteredGTS.No  => gbdRoutes.RegisterGBDController.onPageLoad()
        }
    }
  }

  implicit val pbdGTS: NextPage[AreYouRegisteredGTSId.PBD.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.PBD.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.PoolBetting))
          case AreYouRegisteredGTS.No  => pbdRoutes.RegisterGTSFirstController.onPageLoad()
        }
    }
  }
}
