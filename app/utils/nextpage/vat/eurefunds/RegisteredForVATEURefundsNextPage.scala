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

package utils.nextpage.vat.eurefunds

import config.FrontendAppConfig
import identifiers.RegisteredForVATEURefundsId
import models.vat.RegisteredForVAT
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import utils.{Enrolments, NextPage}

trait RegisteredForVATEURefundsNextPage {

  implicit val registeredForVATEURefunds: NextPage[RegisteredForVATEURefundsId.type, RegisteredForVAT, Call] = {
    new NextPage[RegisteredForVATEURefundsId.type, RegisteredForVAT, Call] {
      override def get(b: RegisteredForVAT)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case RegisteredForVAT.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.EURefunds))
          case RegisteredForVAT.No  => Call("GET", appConfig.vatRegHandoff)
        }
    }
  }
}
