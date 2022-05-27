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

package utils.nextpage.sa

import config.FrontendAppConfig
import identifiers.AreYouSelfEmployedId
import play.api.mvc.{Call, Request}
import models.sa.AreYouSelfEmployed
import utils.NextPage

trait AreYouSelfEmployedNextPage {

  implicit val areYouSelfEmployed: NextPage[AreYouSelfEmployedId.type, AreYouSelfEmployed, Call] = {
    new NextPage[AreYouSelfEmployedId.type, AreYouSelfEmployed, Call] {
      override def get(b: AreYouSelfEmployed)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case AreYouSelfEmployed.Yes => Call("GET", appConfig.getPortalUrl("selectTaxes"))
          case AreYouSelfEmployed.No  => Call("GET", appConfig.getPortalUrl("registerSAForm"))
        }
    }
  }
}
