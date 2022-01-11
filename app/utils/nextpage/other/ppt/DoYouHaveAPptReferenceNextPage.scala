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

package utils.nextpage.other.ppt

import config.FrontendAppConfig
import identifiers.DoYouHaveAPptRegId
import models.other.ppt.DoYouHaveAPptReference
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.NextPage
import controllers.other.ppt.{routes => pptRoutes}


trait DoYouHaveAPptReferenceNextPage {

    implicit val doYouHaveAPptReference: NextPage[DoYouHaveAPptRegId.type, DoYouHaveAPptReference, Call] = {
      new NextPage[DoYouHaveAPptRegId.type, DoYouHaveAPptReference, Call] {
        override def get(b: DoYouHaveAPptReference)(
          implicit appConfig: FrontendAppConfig,
          featureConfig: FeatureConfig,
          request: Request[_]): Call =
          b match {
            case DoYouHaveAPptReference.Yes => Call("GET", appConfig.pptEnrolmentUrl())
            case DoYouHaveAPptReference.No  => pptRoutes.YouNeedAPptRefController.onPageLoad()
          }
      }
    }

}
