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

package utils.nextpage.sa

import config.FrontendAppConfig
import identifiers.SelectSACategoryId
import models.sa.SelectSACategory
import play.api.mvc.{Call, Request}
import utils.NextPage
import controllers.sa.partnership.{routes => saPartnerRoutes}
import controllers.sa.trust.{routes => trustRoutes}

trait SelectSACategoryNextPage {

  implicit val selectSACategory: NextPage[SelectSACategoryId.type, SelectSACategory] = {
    new NextPage[SelectSACategoryId.type, SelectSACategory] {
      override def get(b: SelectSACategory)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case SelectSACategory.Sa          => Call("GET", appConfig.getPortalUrl("businessRegistration"))
          case SelectSACategory.Partnership => saPartnerRoutes.DoYouWantToAddPartnerController.onPageLoad()
          case SelectSACategory.Trust       => trustRoutes.HaveYouRegisteredTrustController.onPageLoad()
        }
    }
  }
}
