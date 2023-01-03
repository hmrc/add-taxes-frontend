/*
 * Copyright 2023 HM Revenue & Customs
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

package utils.nextpage.other.importexports

import config.FrontendAppConfig
import controllers.other.importexports.dan.{routes => danRoutes}
import controllers.other.importexports.emcs.{routes => emcsRoutes}
import controllers.other.importexports.ics.{routes => icsRoutes}
import controllers.other.importexports.ncts.{routes => nctsRoutes}
import controllers.other.importexports.nes.{routes => nesRoutes}
import controllers.other.importexports.ebti.{routes => ebtiRoutes}
import identifiers.DoYouWantToAddImportExportId
import models.other.importexports.DoYouWantToAddImportExport
import play.api.mvc.{Call, Request}
import utils.NextPage

trait DoYouWantToAddImportExportNextPage {

  implicit val doYouWantToAddImportExport
    : NextPage[DoYouWantToAddImportExportId.type, DoYouWantToAddImportExport, Call] = {
    new NextPage[DoYouWantToAddImportExportId.type, models.other.importexports.DoYouWantToAddImportExport, Call] {
      override def get(b: models.other.importexports.DoYouWantToAddImportExport)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case DoYouWantToAddImportExport.ATaR => Call("GET", appConfig.getEoriCommonComponentURL("atar"))
          case DoYouWantToAddImportExport.EMCS => emcsRoutes.DoYouHaveASEEDNumberController.onPageLoad()
          case DoYouWantToAddImportExport.ICS  => icsRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.DDES => danRoutes.DoYouHaveDANController.onPageLoad()
          case DoYouWantToAddImportExport.NOVA => Call("GET", appConfig.getPortalUrl("novaEnrolment"))
          case DoYouWantToAddImportExport.NCTS => nctsRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.eBTI => ebtiRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.NES  => nesRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.ISD  => Call("GET", appConfig.getHmceURL("isd"))
        }
    }
  }
}
