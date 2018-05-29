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

package utils.nextpage.employer.ers

import config.FrontendAppConfig
import controllers.employer.ers.routes
import identifiers.IsBusinessRegisteredForPAYEId
import models.employer.cis.uk.contractor.IsBusinessRegisteredForPAYE
import play.api.mvc.{Call, Request}
import utils.NextPage

trait IsBusinessRegisteredForPAYENextPage {

  implicit val ersIsBusinessRegisteredForPAYE
    : NextPage[IsBusinessRegisteredForPAYEId.ERS.type, IsBusinessRegisteredForPAYE] = {
    new NextPage[IsBusinessRegisteredForPAYEId.ERS.type, IsBusinessRegisteredForPAYE] {
      override def get(
        b: IsBusinessRegisteredForPAYE)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case IsBusinessRegisteredForPAYE.Yes =>
            routes.DoesBusinessManagePAYEController.onPageLoad()
          case IsBusinessRegisteredForPAYE.No =>
            routes.RegisterEmployersPAYEController.onPageLoad()
        }
    }
  }

}
