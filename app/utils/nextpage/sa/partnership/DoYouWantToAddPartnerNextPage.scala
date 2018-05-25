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
import identifiers.DoYouWantToAddPartnerId
import models.sa.partnership.DoYouWantToAddPartner
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}
import controllers.sa.partnership.{routes => saPartnerRoutes}
import models.requests.ServiceInfoRequest
import play.api.mvc.AnyContent
import utils.HmrcEnrolmentType

trait DoYouWantToAddPartnerNextPage {

  implicit val doYouWantToAddPartner
    : NextPage[DoYouWantToAddPartnerId.type, (DoYouWantToAddPartner, ServiceInfoRequest[AnyContent])] = {
    new NextPage[DoYouWantToAddPartnerId.type, (DoYouWantToAddPartner, ServiceInfoRequest[AnyContent])] {
      override def get(b: (DoYouWantToAddPartner, ServiceInfoRequest[AnyContent]))(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        (b._1, Enrolments.hasEnrolments(b._2.request.enrolments, HmrcEnrolmentType.SA, HmrcEnrolmentType.CORP_TAX)) match {
          case (DoYouWantToAddPartner.Yes, true) =>
            Call("GET", appConfig.getIFormUrl("partnership"))
          case (DoYouWantToAddPartner.Yes, false) =>
            Call("GET", appConfig.getPublishedAssetsUrl("partnership"))
          case (DoYouWantToAddPartner.No, _) =>
            saPartnerRoutes.HaveYouRegisteredPartnershipController.onPageLoad()
        }
    }
  }
}
