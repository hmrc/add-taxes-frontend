/*
 * Copyright 2019 HM Revenue & Customs
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

package utils.nextpage.sa

import config.FrontendAppConfig
import controllers.sa.routes.SelectSACategoryController
import controllers.sa.routes.YourSaIsNotInThisAccountController
import identifiers.EnterSAUTRId
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.NextPage

trait EnterSAUTRNextPage {

  type EnrolmentStoreResult = Boolean

  implicit val enterSAUTR: NextPage[EnterSAUTRId.type, EnrolmentStoreResult, Call] = {

    new NextPage[EnterSAUTRId.type, EnrolmentStoreResult, Call] {
      override def get(b: EnrolmentStoreResult)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case false if request.session.get("tryingToAccessSa").contains("true") =>
            YourSaIsNotInThisAccountController.onPageLoad()
          case false => SelectSACategoryController.onPageLoadHasUTR()
          case true  => Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials"))
        }
    }

  }

}
