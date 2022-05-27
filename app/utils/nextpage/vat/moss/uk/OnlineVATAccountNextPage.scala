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

package utils.nextpage.vat.moss.uk

import config.FrontendAppConfig
import identifiers.OnlineVATAccountId
import models.vat.moss.uk.OnlineVATAccount
import play.api.mvc.{Call, Request}
import utils.NextPage
import controllers.vat.moss.ukbased.{routes => vatMossUkRoutes}

trait OnlineVATAccountNextPage {

  implicit val onlineVATAccount: NextPage[OnlineVATAccountId.type, OnlineVATAccount, Call] = {
    new NextPage[OnlineVATAccountId.type, OnlineVATAccount, Call] {
      override def get(b: OnlineVATAccount)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case OnlineVATAccount.Yes => vatMossUkRoutes.AddVATMOSSController.onPageLoad()
          case OnlineVATAccount.No  => vatMossUkRoutes.AddVATFirstController.onPageLoad()
        }
    }
  }
}
