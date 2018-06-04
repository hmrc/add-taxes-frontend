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
import utils.{HmrcEnrolmentType, NextPage}
import uk.gov.hmrc.auth.core.Enrolments

trait HaveYouRegisteredPartnershipNextPage {

  type HaveYouRegisteredPartnershipWithRequest = (HaveYouRegisteredPartnership, Enrolments)

  implicit val haveYouRegisteredPartnership
    : NextPage[HaveYouRegisteredPartnershipId.type, HaveYouRegisteredPartnershipWithRequest] = {

    new NextPage[HaveYouRegisteredPartnershipId.type, HaveYouRegisteredPartnershipWithRequest] {

      override def get(enrolmentDetails: HaveYouRegisteredPartnershipWithRequest)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        (enrolmentDetails._1, hasSACTEnrolments(enrolmentDetails._2)) match {
          case (HaveYouRegisteredPartnership.Yes, _) =>
            Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.SAPartnership))

          case (HaveYouRegisteredPartnership.No, true) =>
            Call("GET", appConfig.getIFormUrl("partnership"))

          case (HaveYouRegisteredPartnership.No, false) =>
            Call("GET", appConfig.getPublishedAssetsUrl("partnershipOther"))
        }
    }

  }

  private def hasSACTEnrolments(enrolments: Enrolments) =
    utils.Enrolments
      .hasEnrolments(enrolments, HmrcEnrolmentType.SA, HmrcEnrolmentType.CORP_TAX)
}
