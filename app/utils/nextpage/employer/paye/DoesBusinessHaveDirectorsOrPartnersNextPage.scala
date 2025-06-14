/*
 * Copyright 2024 HM Revenue & Customs
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
import identifiers.DoesBusinessHaveDirectorsOrPartnersId
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import models.employer.paye.DoesBusinessHaveDirectorsOrPartners
import utils.NextPage
import controllers.employer.paye.{routes => employerPayeRoutes}

trait DoesBusinessHaveDirectorsOrPartnersNextPage {

  implicit val doesBusinessHaveDirectorsOrPartners
    : NextPage[DoesBusinessHaveDirectorsOrPartnersId.type, DoesBusinessHaveDirectorsOrPartners, Call] = {
    new NextPage[DoesBusinessHaveDirectorsOrPartnersId.type, DoesBusinessHaveDirectorsOrPartners, Call] {
      override def get(b: DoesBusinessHaveDirectorsOrPartners)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoesBusinessHaveDirectorsOrPartners.Director =>
            employerPayeRoutes.DoesBusinessHave1To9DirectorsController.onPageLoad()
          case DoesBusinessHaveDirectorsOrPartners.Partner =>
            employerPayeRoutes.DoesYourPartnershipHave2To10PartnersController.onPageLoad()
          case DoesBusinessHaveDirectorsOrPartners.Solo =>
            Call("GET", appConfig.getPortalUrl("selectTaxes"))
        }
    }
  }
}
