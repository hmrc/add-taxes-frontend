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

package utils.nextpage.other.oil

import config.FrontendAppConfig
import controllers.other.oil.routes
import identifiers.HaveYouRegisteredForRebatedOilsId
import models.other.oil.HaveYouRegisteredForRebatedOils
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait HaveYouRegisteredForRebatedOilsNextPage {

  implicit val haveYouRegisteredForRebatedOils
    : NextPage[HaveYouRegisteredForRebatedOilsId.type, HaveYouRegisteredForRebatedOils, Call] = {
    new NextPage[HaveYouRegisteredForRebatedOilsId.type, HaveYouRegisteredForRebatedOils, Call] {
      override def get(b: HaveYouRegisteredForRebatedOils)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case HaveYouRegisteredForRebatedOils.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.RebatedOils))
          case HaveYouRegisteredForRebatedOils.No  => routes.RegisterRebatedOilsController.onPageLoad()
        }
    }
  }

}
