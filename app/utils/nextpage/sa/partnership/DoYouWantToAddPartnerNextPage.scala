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
import identifiers.DoYouWantToAddPartnerId
import models.sa.partnership.DoYouWantToAddPartner
import play.api.mvc.{Call, Request}
import utils.NextPage
import controllers.sa.partnership.{routes => saPartnerRoutes}
import playconfig.featuretoggle.FeatureConfig
import utils.HmrcEnrolmentType
import uk.gov.hmrc.auth.core.Enrolments

trait DoYouWantToAddPartnerNextPage {

  type DoYouWantToAddPartnerWithRequest = (DoYouWantToAddPartner, Enrolments)

  implicit val doYouWantToAddPartner: NextPage[DoYouWantToAddPartnerId.type, DoYouWantToAddPartnerWithRequest, Call] = {

    new NextPage[DoYouWantToAddPartnerId.type, DoYouWantToAddPartnerWithRequest, Call] {

      override def get(enrolmentDetails: DoYouWantToAddPartnerWithRequest)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        enrolmentDetails match {

          case (DoYouWantToAddPartner.Yes, HmrcEnrolmentType.SA() | HmrcEnrolmentType.CORP_TAX()) =>
            Call("GET", appConfig.getIFormUrl("partnership"))

          case (DoYouWantToAddPartner.Yes, _) =>
            Call("GET", appConfig.getPublishedAssetsUrl("partnership"))

          case (DoYouWantToAddPartner.No, _) =>
            saPartnerRoutes.HaveYouRegisteredPartnershipController.onPageLoad()
        }
    }
  }
}
