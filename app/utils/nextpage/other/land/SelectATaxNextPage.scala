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

package utils.nextpage.other.land

import config.FrontendAppConfig
import identifiers.SelectATaxId
import models.other.land.SelectATax
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import controllers.other.land.stampduty.routes._
import utils.NextPage

trait SelectATaxNextPage {

  implicit val selectATax: NextPage[SelectATaxId.type, SelectATax, Call] = {
    new NextPage[SelectATaxId.type, SelectATax, Call] {
      override def get(
        b: SelectATax)(implicit appConfig: FrontendAppConfig,  request: ServiceInfoRequest[_]): Call =
        b match {
          case SelectATax.ATED => Call("GET", appConfig.atedUrl)
          case SelectATax.LBT  => Call("GET", appConfig.revenueScotUrl)
          case SelectATax.SDLT => StampDutyController.onPageLoad()
        }
    }
  }
}
