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

package utils.nextpage.vat

import config.FrontendAppConfig
import controllers.vat.ec.{routes => ecRoutes}
import controllers.vat.eurefunds.{routes => euRoutes}
import controllers.vat.moss.{routes => mossRoutes}
import controllers.vat.rcsl.{routes => rcslRoutes}
import identifiers.WhichVATServicesToAddId
import models.vat.WhichVATServicesToAdd
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}

trait WhichVATServicesToAddNextPage {

  type WhichVATServicesToAddWithEnrolments = (WhichVATServicesToAdd, Enrolments)

  implicit val whichVATServicesToAdd: NextPage[WhichVATServicesToAddId.type, WhichVATServicesToAddWithEnrolments] = {
    new NextPage[WhichVATServicesToAddId.type, WhichVATServicesToAddWithEnrolments] {
      override def get(addPartnerDetails: WhichVATServicesToAddWithEnrolments)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        (addPartnerDetails._1, hasVATEnrolments(addPartnerDetails._2)) match {

          case (WhichVATServicesToAdd.VAT, _) => Call("GET", appConfig.getPortalUrl("businessRegistration"))

          case (WhichVATServicesToAdd.ECSales, true) =>
            Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.ECSales))

          case (WhichVATServicesToAdd.ECSales, false) => ecRoutes.RegisteredForVATECSalesController.onPageLoad()

          case (WhichVATServicesToAdd.EURefunds, true) =>
            Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.EURefunds))

          case (WhichVATServicesToAdd.EURefunds, false) => euRoutes.RegisteredForVATEURefundsController.onPageLoad()

          case (WhichVATServicesToAdd.RCSL, true) => Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.RCSL))

          case (WhichVATServicesToAdd.RCSL, false) => rcslRoutes.RegisteredForVATRCSLController.onPageLoad()

          case (WhichVATServicesToAdd.MOSS, _) => mossRoutes.WhereIsYourBusinessBasedController.onPageLoad()

          case (WhichVATServicesToAdd.NOVA, _) => Call("GET", appConfig.getPortalUrl("novaEnrolment"))
        }
    }
  }

  private def hasVATEnrolments(enrolments: Enrolments) =
    utils.Enrolments
      .hasEnrolments(enrolments, HmrcEnrolmentType.VAT)
}
