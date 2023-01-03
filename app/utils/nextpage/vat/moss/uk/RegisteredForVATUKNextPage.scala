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

package utils.nextpage.vat.moss.uk

import config.FrontendAppConfig
import identifiers.RegisteredForVATUkId
import models.vat.moss.uk.RegisteredForVATUk
import play.api.mvc.{Call, Request}
import utils.NextPage
import controllers.vat.moss.ukbased.{routes => vatMossUkRoutes}

trait RegisteredForVATUKNextPage {

  implicit val registeredForVATUk: NextPage[RegisteredForVATUkId.type, RegisteredForVATUk, Call] = {
    new NextPage[RegisteredForVATUkId.type, RegisteredForVATUk, Call] {
      override def get(b: RegisteredForVATUk)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case RegisteredForVATUk.Yes => vatMossUkRoutes.OnlineVATAccountController.onPageLoad()
          case RegisteredForVATUk.No  => vatMossUkRoutes.RegisterForVATController.onPageLoad()
        }
    }
  }
}
