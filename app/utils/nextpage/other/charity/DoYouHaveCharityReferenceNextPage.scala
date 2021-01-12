/*
 * Copyright 2021 HM Revenue & Customs
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

package utils.nextpage.other.charity

import config.FrontendAppConfig
import identifiers.DoYouHaveCharityReferenceId
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}
import controllers.other.charity.{routes => charityRoutes}
import models.other.charity.DoYouHaveCharityReference
import playconfig.featuretoggle.FeatureConfig

trait DoYouHaveCharityReferenceNextPage {

  implicit val doYouHaveCharityReference
    : NextPage[DoYouHaveCharityReferenceId.type, DoYouHaveCharityReference, Call] = {
    new NextPage[DoYouHaveCharityReferenceId.type, DoYouHaveCharityReference, Call] {
      override def get(b: DoYouHaveCharityReference)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoYouHaveCharityReference.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.Charities))
          case DoYouHaveCharityReference.No  => charityRoutes.RegisterForCharityController.onPageLoad()
        }
    }
  }

}
