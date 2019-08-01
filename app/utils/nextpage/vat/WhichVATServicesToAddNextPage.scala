/*
 * Copyright 2019 HM Revenue & Customs
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
import controllers.vat.{routes => vatRoutes}
import controllers.vat.eurefunds.{routes => euRoutes}
import controllers.vat.giant.{routes => giantRoutes}
import controllers.vat.moss.{routes => mossRoutes}
import controllers.vat.moss.ukbased.{routes => ukbasedRoutes}
import controllers.vat.moss.newaccount.{routes => newAccountRoutes}
import controllers.vat.rcsl.{routes => rcslRoutes}
import identifiers.WhichVATServicesToAddId
import models.vat.WhichVATServicesToAdd
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}

trait WhichVATServicesToAddNextPage {

  type WhichVATServicesToAddWithRequests = (WhichVATServicesToAdd, Option[AffinityGroup], Enrolments)

  implicit val whichVATServicesToAdd
    : NextPage[WhichVATServicesToAddId.type, WhichVATServicesToAddWithRequests, Call] = {
    new NextPage[WhichVATServicesToAddId.type, WhichVATServicesToAddWithRequests, Call] {
      override def get(b: WhichVATServicesToAddWithRequests)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call = {

        val (serviceToAdd, affinity, enrolments) = b

        serviceToAdd match {
          case WhichVATServicesToAdd.VAT       => vatRoutes.DoYouHaveVATRegNumberController.onPageLoad()
          case WhichVATServicesToAdd.ECSales   => getECSalesCall(enrolments)
          case WhichVATServicesToAdd.GIANT     => getVATGIANTCall(affinity)
          case WhichVATServicesToAdd.EURefunds => getEURefundsCall(enrolments)
          case WhichVATServicesToAdd.RCSL      => getRCSLCall(enrolments)
          case WhichVATServicesToAdd.MOSS      => getVATMOSSCall(affinity, enrolments)
          case WhichVATServicesToAdd.NOVA      => Call("GET", appConfig.getPortalUrl("novaEnrolment"))
        }
      }
    }
  }

  def getECSalesCall(enrolments: Enrolments)(implicit appConfig: FrontendAppConfig): Call =
    enrolments match {
      case HmrcEnrolmentType.VAT() => Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.ECSales))
      case _                       => ecRoutes.RegisteredForVATECSalesController.onPageLoad()
    }

  def getVATGIANTCall(affinity: Option[AffinityGroup]): Call =
    affinity match {
      case Some(AffinityGroup.Individual) => giantRoutes.SetupNewAccountController.onPageLoad()
      case _                              => giantRoutes.WhatIsYourOrganisationController.onPageLoad()
    }

  def getEURefundsCall(enrolments: Enrolments)(implicit appConfig: FrontendAppConfig): Call =
    enrolments match {
      case HmrcEnrolmentType.VAT() => Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.EURefunds))
      case _                       => euRoutes.RegisteredForVATEURefundsController.onPageLoad()
    }

  def getRCSLCall(enrolments: Enrolments)(implicit appConfig: FrontendAppConfig): Call =
    enrolments match {
      case HmrcEnrolmentType.VAT() => Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.RCSL))
      case _                       => rcslRoutes.RegisteredForVATRCSLController.onPageLoad()
    }

  def getVATMOSSCall(affinity: Option[AffinityGroup], enrolments: Enrolments): Call =
    (affinity, enrolments) match {
      case (Some(AffinityGroup.Individual), _) => newAccountRoutes.SetUpANewAccountController.onPageLoad()
      case (_, HmrcEnrolmentType.VAT())        => ukbasedRoutes.AlreadyRegisteredForVATMossController.onPageLoad()
      case (_, _)                              => mossRoutes.WhereIsYourBusinessBasedController.onPageLoad()
    }
}
