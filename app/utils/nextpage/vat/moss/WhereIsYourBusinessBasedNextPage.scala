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

package utils.nextpage.vat.moss

import config.FrontendAppConfig
import controllers.vat.moss.ukbased.{routes => ukBasedRoutes}
import controllers.vat.moss.iom.{routes => iomRoutes}
import controllers.vat.moss.noneu.{routes => noneuRoutes}
import controllers.vat.moss.eu.{routes => euRoutes}
import identifiers.WhereIsYourBusinessBasedId
import models.vat.moss.WhereIsYourBusinessBased
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait WhereIsYourBusinessBasedNextPage {

  implicit val whereIsYourBusinessBased: NextPage[WhereIsYourBusinessBasedId.type, WhereIsYourBusinessBased, Call] = {
    new NextPage[WhereIsYourBusinessBasedId.type, WhereIsYourBusinessBased, Call] {
      override def get(b: WhereIsYourBusinessBased)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case WhereIsYourBusinessBased.UK    => ukBasedRoutes.RegisteredForVATUkController.onPageLoad()
          case WhereIsYourBusinessBased.EU    => euRoutes.RegisterInHomeCountryController.onPageLoad()
          case WhereIsYourBusinessBased.NonEu => noneuRoutes.HaveYouRegisteredForVATMOSSController.onPageLoad()
          case WhereIsYourBusinessBased.Iom   => iomRoutes.RegisteredForVATController.onPageLoad()
        }
    }
  }
}
