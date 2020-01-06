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

package utils.nextpage.other.importexports.emcs

import config.FrontendAppConfig
import controllers.other.importexports.emcs.{routes => emcsRoutes}
import identifiers.DoYouHaveASEEDNumberId
import models.other.importexports.emcs.DoYouHaveASEEDNumber
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}

trait DoYouHaveASEEDNumberNextPage {

  implicit val doYouHaveASEEDNumber: NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber, Call] = {
    new NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber, Call] {
      override def get(b: DoYouHaveASEEDNumber)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoYouHaveASEEDNumber.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.ExciseMovementControlSystem))
          case DoYouHaveASEEDNumber.No => emcsRoutes.RegisterExciseMovementControlSystemController.onPageLoad()
        }
    }
  }
}
