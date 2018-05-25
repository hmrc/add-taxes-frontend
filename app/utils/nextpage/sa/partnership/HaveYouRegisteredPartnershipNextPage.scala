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
import models.requests.ServiceInfoRequest
import models.sa.partnership.HaveYouRegisteredPartnership
import play.api.mvc.{AnyContent, Call, Request}
import utils.{Enrolments, HmrcEnrolmentType, NextPage}

trait HaveYouRegisteredPartnershipNextPage {

  implicit val haveYouRegisteredPartnership
    : NextPage[HaveYouRegisteredPartnershipId.type, (HaveYouRegisteredPartnership, ServiceInfoRequest[AnyContent])] = {
    new NextPage[HaveYouRegisteredPartnershipId.type, (HaveYouRegisteredPartnership, ServiceInfoRequest[AnyContent])] {
      override def get(b: (HaveYouRegisteredPartnership, ServiceInfoRequest[AnyContent]))(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        (b._1, Enrolments.hasEnrolments(b._2.request.enrolments, HmrcEnrolmentType.SA, HmrcEnrolmentType.CORP_TAX)) match {
          case (HaveYouRegisteredPartnership.Yes, _) =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.SAPartnership))
          case (HaveYouRegisteredPartnership.No, true) =>
            Call("GET", appConfig.getIFormUrl("partnership"))
          case (HaveYouRegisteredPartnership.No, false) =>
            Call("GET", appConfig.getPublishedAssetsUrl("partnershipOther"))
        }
    }
  }
}
