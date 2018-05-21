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

package utils.nextpage.employer.pension

import config.FrontendAppConfig
import identifiers.WhichPensionSchemeToAddId
import models.employer.pension.WhichPensionSchemeToAdd
import play.api.mvc.{Call, Request}
import utils.NextPage

trait WhichPensionSchemeToAddNextPage {

  implicit val whichPensionSchemeToAdd: NextPage[WhichPensionSchemeToAddId.type, WhichPensionSchemeToAdd] = {
    new NextPage[WhichPensionSchemeToAddId.type, WhichPensionSchemeToAdd] {
      override def get(b: WhichPensionSchemeToAdd)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case WhichPensionSchemeToAdd.Administrators =>
            Call("GET", appConfig.getPortalUrl("pensionAdministrators"))
          case WhichPensionSchemeToAdd.Practitioners =>
            Call("GET", appConfig.getPortalUrl("pensionPractitioners"))
        }
    }
  }
}
