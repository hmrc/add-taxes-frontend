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

package utils.nextpage.employer

import config.FrontendAppConfig
import controllers.employer.cis.{routes => cisRoutes}
import controllers.employer.ers.{routes => ersRoutes}
import controllers.employer.intermediaries.{routes => intRoutes}
import controllers.employer.paye.{routes => payeRoutes}
import controllers.routes
import identifiers.WhatEmployerTaxDoYouWantToAddId
import models.employer.WhatEmployerTaxDoYouWantToAdd
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}

trait WhatEmployerTaxDoYouWantToAddNextPage {

  type WhatEmployerTaxDoYouWantToAddWithEnrolment = (WhatEmployerTaxDoYouWantToAdd, Enrolments)

  implicit val whatEmployerTaxDoYouWantToAdd
    : NextPage[WhatEmployerTaxDoYouWantToAddId.type, WhatEmployerTaxDoYouWantToAddWithEnrolment, Call] = {
    new NextPage[WhatEmployerTaxDoYouWantToAddId.type, WhatEmployerTaxDoYouWantToAddWithEnrolment, Call] {
      override def get(details: WhatEmployerTaxDoYouWantToAddWithEnrolment)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        details match {
          case (WhatEmployerTaxDoYouWantToAdd.EPAYE, _) =>
              payeRoutes.DoYouHavePAYEReferenceController.onPageLoad()
          case (WhatEmployerTaxDoYouWantToAdd.CIS, _) =>
            cisRoutes.IsYourBusinessInUKController.onPageLoad()
          case (WhatEmployerTaxDoYouWantToAdd.ERS, HmrcEnrolmentType.EPAYE()) =>
            getEnrolERSCall(details)
          case (WhatEmployerTaxDoYouWantToAdd.ERS, _) =>
            ersRoutes.IsBusinessRegisteredForPAYEController.onPageLoad()
          case (WhatEmployerTaxDoYouWantToAdd.EIA, HmrcEnrolmentType.EPAYE()) =>
            Call("GET", appConfig.eiUrl)
          case (WhatEmployerTaxDoYouWantToAdd.EIA, _) =>
            intRoutes.IsBusinessRegisteredForPAYEController.onPageLoad()
        }
    }
  }

  def getEnrolERSCall(details: WhatEmployerTaxDoYouWantToAddWithEnrolment)
                     (implicit appConfig: FrontendAppConfig, request: Request[_]): Call = {

    def taxOfficeNumber: Option[String] =
      details._2
        .getEnrolment(utils.Enrolments.EPAYE.toString)
        .map(
          e =>
            e.identifiers
              .find(_.key == "TaxOfficeNumber")
              .getOrElse(
                throw new IllegalArgumentException("Cannot find 'TaxOfficeNumber' Enrolment identifier for EPAYE"))
              .value)

    def taxOfficeReference: Option[String] =
      details._2
        .getEnrolment(utils.Enrolments.EPAYE.toString)
        .map(
          e =>
            e.identifiers
              .find(_.key == "TaxOfficeReference")
              .getOrElse(
                throw new IllegalArgumentException("Cannot find 'TaxOfficeReference' Enrolment identifier for EPAYE"))
              .value)

    (taxOfficeReference, taxOfficeNumber) match {
      case (Some(ref), Some(num)) =>
        val fullRef: String = num + "/" + ref
        Call("GET", appConfig.getPortalUrl("enrolERS", fullRef))

      case _ => routes.UnauthorisedController.onPageLoad
    }
  }
}
