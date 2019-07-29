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

package utils.nextpage.sa.partnership

import config.FrontendAppConfig
import identifiers.HaveYouRegisteredPartnershipId
import models.requests.ServiceInfoRequest
import models.sa.partnership.HaveYouRegisteredPartnership
import play.api.mvc.{AnyContent, Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.{HmrcEnrolmentType, NextPage}
import uk.gov.hmrc.auth.core.Enrolments

trait HaveYouRegisteredPartnershipNextPage {

  type HaveYouRegisteredPartnershipWithRequest = (HaveYouRegisteredPartnership, Enrolments)

  implicit val haveYouRegisteredPartnership
    : NextPage[HaveYouRegisteredPartnershipId.type, HaveYouRegisteredPartnershipWithRequest, Call] = {

    new NextPage[HaveYouRegisteredPartnershipId.type, HaveYouRegisteredPartnershipWithRequest, Call] {

      override def get(enrolmentDetails: HaveYouRegisteredPartnershipWithRequest)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        enrolmentDetails match {
          case (HaveYouRegisteredPartnership.Yes, _) =>
            Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.SAPartnership))

          case (HaveYouRegisteredPartnership.No, HmrcEnrolmentType.SA() | HmrcEnrolmentType.CORP_TAX()) =>
            Call("GET", appConfig.getIFormUrl("partnership"))

          case (HaveYouRegisteredPartnership.No, _) =>
            Call("GET", appConfig.getPublishedAssetsUrl("partnershipOther"))
        }
    }
  }
}
