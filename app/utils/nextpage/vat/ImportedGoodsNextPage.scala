/*
 * Copyright 2021 HM Revenue & Customs
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
import identifiers.ImportedGoodsId
import play.api.mvc.{Call, Request}
import models.vat.ImportedGoods
import utils.NextPage
import controllers.vat.{routes => vatRoutes}
import playconfig.featuretoggle.FeatureConfig

trait ImportedGoodsNextPage {

  implicit val importedGoods: NextPage[ImportedGoodsId.type, ImportedGoods, Call] = {
    new NextPage[ImportedGoodsId.type, ImportedGoods, Call] {
      override def get(b: ImportedGoods)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case ImportedGoods.Yes => vatRoutes.CannotRegisterVATController.onPageLoad()
          case ImportedGoods.No  => vatRoutes.ClaimRefundController.onPageLoad()
        }
    }
  }
}
