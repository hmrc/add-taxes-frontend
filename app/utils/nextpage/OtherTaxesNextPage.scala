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

package utils.nextpage

import config.FrontendAppConfig
import controllers.other.oil.{routes => oilRoutes}
import controllers.other.land.{routes => landRoutes}
import controllers.other.aeoi.{routes => aeoiRoutes}
import controllers.other.charity.{routes => charityRoutes}
import controllers.other.gambling.{routes => gamblingRoutes}
import controllers.other.importexports.{routes => importexportsRoutes}
import controllers.other.alcohol.awrs.{routes => alcoholRoutes}
import controllers.other.ctf.{routes => ctfRoutes}
import controllers.other.ppt.{routes => pptRoutes}
import identifiers.OtherTaxesId
import models.OtherTaxes
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.NextPage

trait OtherTaxesNextPage {

  implicit val otherTaxes: NextPage[OtherTaxesId.type, OtherTaxes, Call] = {
    new NextPage[OtherTaxesId.type, OtherTaxes, Call] {
      override def get(
        b: OtherTaxes)(implicit appConfig: FrontendAppConfig, featureConfig: FeatureConfig, request: Request[_]): Call =
        b match {
          case OtherTaxes.AlcoholAndTobacco                            => alcoholRoutes.SelectAlcoholSchemeController.onPageLoad()
          case OtherTaxes.AutomaticExchangeOfInformation               => aeoiRoutes.HaveYouRegisteredAEOIController.onPageLoad()
          case OtherTaxes.Charities                                    => charityRoutes.DoYouHaveCharityReferenceController.onPageLoad()
          case OtherTaxes.GamblingAndGaming                            => gamblingRoutes.SelectGamblingOrGamingDutyController.onPageLoad()
          case OtherTaxes.ChildTrustFund                               => ctfRoutes.AreYouApprovedCTFController.onPageLoad()
          case OtherTaxes.HousingAndLand                               => landRoutes.SelectATaxController.onPageLoad()
          case OtherTaxes.ImportsExports                               => importexportsRoutes.DoYouWantToAddImportExportController.onPageLoad()
          case OtherTaxes.OilAndFuel                                   => oilRoutes.SelectAnOilServiceController.onPageLoad()
          case OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration => Call("GET", appConfig.fulfilmentHouse)
          case OtherTaxes.PODS                                         => Call("GET", appConfig.pods)
          case OtherTaxes.PPT                                          => pptRoutes.DoYouHaveAPPTRefController.onPageLoad()
        }
    }
  }
}
