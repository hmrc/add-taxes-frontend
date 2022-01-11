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

package utils.nextpage.vat.moss.iom

import config.FrontendAppConfig
import controllers.vat.moss.iom.{routes => vatMossRoutes}
import identifiers.RegisteredForVATId
import play.api.mvc.{Call, Request}
import models.vat.moss.iom.RegisteredForVAT
import playconfig.featuretoggle.FeatureConfig
import utils.NextPage

trait RegisteredForVATNextPage {

  implicit val registeredForVAT: NextPage[RegisteredForVATId.type, RegisteredForVAT, Call] = {
    new NextPage[RegisteredForVATId.type, RegisteredForVAT, Call] {
      override def get(b: RegisteredForVAT)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case RegisteredForVAT.Yes => vatMossRoutes.AlreadyRegisteredForVATMossController.onPageLoad()
          case RegisteredForVAT.No  => vatMossRoutes.RegisterForVATController.onPageLoad()
        }
    }
  }
}
