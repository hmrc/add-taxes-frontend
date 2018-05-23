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

package utils.nextpage

import config.FrontendAppConfig
import controllers.other.oil.routes
import identifiers.OtherTaxesId
import models.OtherTaxes
import play.api.mvc.{Call, Request}
import utils.NextPage

trait OtherTaxesNextPage {

  implicit val otherTaxes: NextPage[OtherTaxesId.type, OtherTaxes] = {
    new NextPage[OtherTaxesId.type, OtherTaxes] {
      override def get(b: OtherTaxes)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case models.OtherTaxes.AlcoholAndTobacco                            => Call("GET", appConfig.getBusinessAccountUrl("alcohol"))
          case models.OtherTaxes.AutomaticExchangeOfInformation               => Call("GET", appConfig.getBusinessAccountUrl("aeoi"))
          case models.OtherTaxes.Charities                                    => Call("GET", appConfig.getBusinessAccountUrl("charities"))
          case models.OtherTaxes.GamblingAndGaming                            => Call("GET", appConfig.getBusinessAccountUrl("gambling"))
          case models.OtherTaxes.HousingAndLand                               => Call("GET", appConfig.getBusinessAccountUrl("land"))
          case models.OtherTaxes.ImportsExports                               => Call("GET", appConfig.getBusinessAccountUrl("import-export"))
          case models.OtherTaxes.OilAndFuel                                   => routes.SelectAnOilServiceController.onPageLoad()
          case models.OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration => Call("GET", appConfig.fulfilmentHouse)
        }
    }
  }
}
