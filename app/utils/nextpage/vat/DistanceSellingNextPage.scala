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

package utils.nextpage.vat

import config.FrontendAppConfig
import identifiers.DistanceSellingId
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import models.vat.DistanceSelling
import utils.NextPage
import controllers.vat.{routes => vatRoutes}

trait DistanceSellingNextPage {

  implicit val distanceSelling: NextPage[DistanceSellingId.type, DistanceSelling, Call] = {
    new NextPage[DistanceSellingId.type, DistanceSelling, Call] {
      override def get(b: DistanceSelling)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DistanceSelling.Yes => vatRoutes.CannotRegisterVATController.onPageLoad()
          case DistanceSelling.No  => vatRoutes.ImportedGoodsController.onPageLoad()
        }
    }
  }
}
