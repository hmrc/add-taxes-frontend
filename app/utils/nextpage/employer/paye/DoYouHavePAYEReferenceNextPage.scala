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

package utils.nextpage.employer.paye

import config.FrontendAppConfig
import identifiers.DoYouHavePAYEReferenceId
import play.api.mvc.{Call, Request}
import models.employer.paye.DoYouHavePAYEReference
import utils.{Enrolments, NextPage}
import controllers.employer.paye.{routes => employerRoutes}
import playconfig.featuretoggle.FeatureConfig

trait DoYouHavePAYEReferenceNextPage {

  implicit val doYouHavePAYEReference: NextPage[DoYouHavePAYEReferenceId.type, DoYouHavePAYEReference, Call] = {
    new NextPage[DoYouHavePAYEReferenceId.type, DoYouHavePAYEReference, Call] {
      override def get(b: DoYouHavePAYEReference)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoYouHavePAYEReference.Yes =>
            if (appConfig.employerPayeJourneyEnabled) {
              Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.EPAYE))
            } else {
              Call("GET", appConfig.getPortalUrl("businessRegistration"))
            }
          case DoYouHavePAYEReference.No => employerRoutes.DoesBusinessHaveDirectorsOrPartnersController.onPageLoad()
        }
    }
  }
}
