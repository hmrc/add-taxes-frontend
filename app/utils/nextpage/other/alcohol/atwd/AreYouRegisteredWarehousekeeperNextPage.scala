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

package utils.nextpage.other.alcohol.atwd

import config.FrontendAppConfig
import identifiers.AreYouRegisteredWarehousekeeperId
import models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}
import controllers.other.alcohol.atwd.{routes => atwdRoutes}

trait AreYouRegisteredWarehousekeeperNextPage {

  implicit val areYouRegisteredWarehousekeeper
    : NextPage[AreYouRegisteredWarehousekeeperId.type, AreYouRegisteredWarehousekeeper] = {
    new NextPage[AreYouRegisteredWarehousekeeperId.type, AreYouRegisteredWarehousekeeper] {
      override def get(
        b: AreYouRegisteredWarehousekeeper)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredWarehousekeeper.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.ATWD))

          case AreYouRegisteredWarehousekeeper.No => atwdRoutes.RegisterWarehousekeeperController.onPageLoad()
        }
    }
  }

}
