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

package utils.nextpage.other.importexports.nes

import config.FrontendAppConfig
import controllers.other.importexports.nes.{routes => nesRoutes}
import identifiers.DoYouHaveCHIEFRoleId
import models.other.importexports.nes.DoYouHaveCHIEFRole
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait DoYouHaveCHIEFRoleNextPage {

  implicit val doYouHaveCHIEFHasEORIRole: NextPage[DoYouHaveCHIEFRoleId.HasEORI.type, DoYouHaveCHIEFRole] = {
    new NextPage[DoYouHaveCHIEFRoleId.HasEORI.type, DoYouHaveCHIEFRole] {
      override def get(b: DoYouHaveCHIEFRole)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveCHIEFRole.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.NewExportSystem))
          case DoYouHaveCHIEFRole.No  => nesRoutes.GetCHIEFRoleController.onPageLoad()
        }
    }
  }

  implicit val doYouHaveCHIEFNoEORIRole: NextPage[DoYouHaveCHIEFRoleId.NoEORI.type, DoYouHaveCHIEFRole] = {
    new NextPage[DoYouHaveCHIEFRoleId.NoEORI.type, DoYouHaveCHIEFRole] {
      override def get(b: DoYouHaveCHIEFRole)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveCHIEFRole.Yes => nesRoutes.RegisterEORIController.onPageLoad()
          case DoYouHaveCHIEFRole.No  => nesRoutes.GetEoriAndChiefRoleController.onPageLoad()
        }
    }
  }
}
