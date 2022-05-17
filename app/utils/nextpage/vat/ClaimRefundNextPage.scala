/*
 * Copyright 2022 HM Revenue & Customs
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

package utils.nextpage.vat

import config.FrontendAppConfig
import identifiers.ClaimRefundId
import play.api.mvc.{Call, Request}
import models.vat.ClaimRefund
import utils.NextPage
import controllers.vat.{routes => vatRoutes}

trait ClaimRefundNextPage {

  implicit val claimRefund: NextPage[ClaimRefundId.type, ClaimRefund, Call] = {
    new NextPage[ClaimRefundId.type, ClaimRefund, Call] {
      override def get(b: ClaimRefund)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case ClaimRefund.Yes => vatRoutes.CannotRegisterVATController.onPageLoad()
          case ClaimRefund.No  => vatRoutes.CanRegisterForVATController.onPageLoad()
        }
    }
  }
}
