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
import identifiers.DoesYourPartnershipHave2To10PartnersId
import play.api.mvc.{Call, Request}
import models.employer.paye.DoesYourPartnershipHave2To10Partners
import utils.NextPage
import controllers.employer.{routes => employerPayeRoutes}

trait DoesYourPartnershipHave2To10PartnersNextPage {

  implicit val doesYourPartnershipHave2To10Partners
    : NextPage[DoesYourPartnershipHave2To10PartnersId.type, DoesYourPartnershipHave2To10Partners, Call] = {
    new NextPage[DoesYourPartnershipHave2To10PartnersId.type, DoesYourPartnershipHave2To10Partners, Call] {
      override def get(
        b: DoesYourPartnershipHave2To10Partners)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoesYourPartnershipHave2To10Partners.Yes => Call("GET", appConfig.getPortalUrl("selectTaxes"))
          case DoesYourPartnershipHave2To10Partners.No =>
            employerPayeRoutes.PartnershipRegisterByPhoneController.onPageLoad()
        }
    }
  }
}
