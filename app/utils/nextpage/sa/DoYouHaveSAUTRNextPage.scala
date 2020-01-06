/*
 * Copyright 2020 HM Revenue & Customs
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
import identifiers.DoYouHaveSAUTRId
import controllers.sa.routes._
import play.api.mvc.{Call, Request}
import models.sa.DoYouHaveSAUTR
import playconfig.featuretoggle.FeatureConfig
import utils.NextPage

trait DoYouHaveSAUTRNextPage {

  implicit val doYouHaveSAUTR: NextPage[DoYouHaveSAUTRId.type, DoYouHaveSAUTR, Call] = {
    new NextPage[DoYouHaveSAUTRId.type, DoYouHaveSAUTR, Call] {
      override def get(b: DoYouHaveSAUTR)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoYouHaveSAUTR.Yes => EnterSAUTRController.onPageLoad()
          case DoYouHaveSAUTR.No  => SelectSACategoryController.onPageLoadNoUTR()
        }
    }
  }
}
