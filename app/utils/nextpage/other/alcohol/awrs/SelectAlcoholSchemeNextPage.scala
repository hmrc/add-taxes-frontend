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

package utils.nextpage.other.alcohol.awrs

import config.FrontendAppConfig
import identifiers.SelectAlcoholSchemeId
import models.other.alcohol.awrs.SelectAlcoholScheme
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import utils.NextPage
import controllers.other.alcohol.atwd.{routes => atwdRoutes}

trait SelectAlcoholSchemeNextPage {

  implicit val selectAlcoholScheme: NextPage[SelectAlcoholSchemeId.type, SelectAlcoholScheme, Call] = {
    new NextPage[SelectAlcoholSchemeId.type, SelectAlcoholScheme, Call] {
      override def get(b: SelectAlcoholScheme)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case SelectAlcoholScheme.ATWD => atwdRoutes.AreYouRegisteredWarehousekeeperController.onPageLoad()
          case SelectAlcoholScheme.AWRS => Call("GET", appConfig.getBusinessAccountUrl("awrs"))
          case SelectAlcoholScheme.AD => Call("GET", appConfig.getAdrUrl)
        }
    }
  }
}
