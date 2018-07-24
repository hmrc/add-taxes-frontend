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

package utils.nextpage.employer.cis.uk.subcontractor

import config.FrontendAppConfig
import identifiers.WasTurnoverMoreAfterVATId
import play.api.mvc.{Call, Request}
import models.employer.cis.uk.subcontractor.WasTurnoverMoreAfterVAT
import utils.NextPage
import controllers.employer.cis.ukbased.subcontractor.routes._

trait WasTurnoverMoreAfterVATNextPage {

  implicit val wasTurnoverMoreAfterVAT: NextPage[WasTurnoverMoreAfterVATId.type, WasTurnoverMoreAfterVAT, Call] = {
    new NextPage[WasTurnoverMoreAfterVATId.type, WasTurnoverMoreAfterVAT, Call] {
      override def get(b: WasTurnoverMoreAfterVAT)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case WasTurnoverMoreAfterVAT.Yes => DoYouWantToBePaidNetOrGrossController.onPageLoad()
          case WasTurnoverMoreAfterVAT.No  => Call("GET", appConfig.getGovUKUrl("cisRegisterPaidGrossOrLowTurnOver"))
        }
    }
  }
}
