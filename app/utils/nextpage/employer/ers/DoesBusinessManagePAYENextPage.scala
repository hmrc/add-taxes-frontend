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
import identifiers.DoesBusinessManagePAYEId
import play.api.mvc.{Call, Request}
import models.employer.ers.DoesBusinessManagePAYE
import utils.NextPage

trait DoesBusinessManagePAYENextPage {

  implicit val ersDoesBusinessManagePAYE: NextPage[DoesBusinessManagePAYEId.type, DoesBusinessManagePAYE] = {
    new NextPage[DoesBusinessManagePAYEId.type, DoesBusinessManagePAYE] {
      override def get(b: DoesBusinessManagePAYE)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoesBusinessManagePAYE.Yes => routes.UseEmployersPAYEController.onPageLoad()
          case DoesBusinessManagePAYE.No  => routes.AddEmployersPAYEController.onPageLoad()
        }
    }
  }
}
