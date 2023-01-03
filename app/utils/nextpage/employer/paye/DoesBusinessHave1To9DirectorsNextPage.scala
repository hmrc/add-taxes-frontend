/*
 * Copyright 2023 HM Revenue & Customs
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
import identifiers.DoesBusinessHave1To9DirectorsId
import play.api.mvc.{Call, Request}
import models.employer.paye.DoesBusinessHave1To9Directors
import utils.NextPage
import controllers.employer.{routes => employerPayeRoutes}


trait DoesBusinessHave1To9DirectorsNextPage {

  implicit val doesBusinessHave1To9Directors
    : NextPage[DoesBusinessHave1To9DirectorsId.type, DoesBusinessHave1To9Directors, Call] = {
    new NextPage[DoesBusinessHave1To9DirectorsId.type, DoesBusinessHave1To9Directors, Call] {
      override def get(b: DoesBusinessHave1To9Directors)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case DoesBusinessHave1To9Directors.Yes => Call("GET", appConfig.getPortalUrl("selectTaxes"))
          case DoesBusinessHave1To9Directors.No  => employerPayeRoutes.DirectorsRegisterByPhoneController.onPageLoad()
        }
    }
  }
}
