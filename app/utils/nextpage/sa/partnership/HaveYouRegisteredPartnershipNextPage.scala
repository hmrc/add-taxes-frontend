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

package utils.nextpage.sa.partnership

import config.FrontendAppConfig
import identifiers.HaveYouRegisteredPartnershipId
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait HaveYouRegisteredPartnershipNextPage {

  implicit val haveYouRegisteredPartnership
    : NextPage[HaveYouRegisteredPartnershipId.type, models.sa.partnership.HaveYouRegisteredPartnership] = {
    new NextPage[HaveYouRegisteredPartnershipId.type, models.sa.partnership.HaveYouRegisteredPartnership] {
      override def get(b: models.sa.partnership.HaveYouRegisteredPartnership)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case models.sa.partnership.HaveYouRegisteredPartnership.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.SAPartnership))
          case models.sa.partnership.HaveYouRegisteredPartnership.No =>
            Call("GET", appConfig.getPublishedAssetsUrl("partnershipOther"))
        }
    }
  }
}
