/*
 * Copyright 2024 HM Revenue & Customs
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

package models.other.importexports

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.{ARSContentSwitch, AtarSwitch, NewCTCEnrolmentForNCTSJourney}
import config.featureToggles.FeatureToggleSupport
import models.requests.ServiceInfoRequest
import utils.Enrolments.CommonTransitConvention
import utils.{Enumerable, RadioOption, WithName}

sealed trait DoYouWantToAddImportExport

object DoYouWantToAddImportExport extends FeatureToggleSupport{
  case object ATaR extends WithName("ATaR") with DoYouWantToAddImportExport
  case object ARS extends WithName("ARS") with DoYouWantToAddImportExport
  case object EMCS extends WithName("EMCS") with DoYouWantToAddImportExport
  case object ICS extends WithName("ICS") with DoYouWantToAddImportExport
  case object DDES extends WithName("DDES") with DoYouWantToAddImportExport
  case object NOVA extends WithName("NOVA") with DoYouWantToAddImportExport
  case object NCTS extends WithName("NCTS") with DoYouWantToAddImportExport
  case object eBTI extends WithName("eBTI") with DoYouWantToAddImportExport
  case object NES extends WithName("NES") with DoYouWantToAddImportExport
  case object ISD extends WithName("ISD") with DoYouWantToAddImportExport

  val values: List[DoYouWantToAddImportExport] = List(
    ATaR,
    ARS,
    EMCS,
    ICS,
    DDES,
    NOVA,
    NCTS,
    eBTI,
    NES,
    ISD
  )

  object RadioFilters {

    def removeATARIfNotEnabled()(implicit appConfig: FrontendAppConfig): Seq[DoYouWantToAddImportExport] = {
      if (isEnabled(AtarSwitch)) Seq() else Seq(ATaR)
    }

    def replaceATARWithARSIfEnabled()(implicit request: ServiceInfoRequest[_], appConfig: FrontendAppConfig): Seq[DoYouWantToAddImportExport] = {
      if (isEnabled(ARSContentSwitch)) {
        infoLog("[DoYouWantToAddImportExport][replaceATARWithARSIfEnabled] ARSContentSwitch enabled. User shown ARS instead of ATAR")
        Seq(ATaR)
      } else {
        Seq(ARS)
      }
    }

    def removeNCTSIfUserHasCTCEnrolment()(implicit request: ServiceInfoRequest[_], appConfig: FrontendAppConfig): Seq[DoYouWantToAddImportExport] = {
      val hasCTCEnrolment = request.hasEnrolments(Seq(CommonTransitConvention))
      (isEnabled(NewCTCEnrolmentForNCTSJourney), hasCTCEnrolment) match {
        case (true, true) =>
          infoLog("[DoYouWantToAddImportExport][removeNCTSIfUserHasCTCEnrolment] user has CTC enrolment. NCTS radio not visible")
          Seq(NCTS)
        case _ => Seq()
      }
    }

  }

  def filteredRadios()(implicit request: ServiceInfoRequest[_], appConfig: FrontendAppConfig): Seq[DoYouWantToAddImportExport] = {
    val enrolmentsToRemove: Seq[DoYouWantToAddImportExport] =
      Seq(eBTI) ++
        RadioFilters.removeATARIfNotEnabled() ++
        RadioFilters.replaceATARWithARSIfEnabled() ++
        RadioFilters.removeNCTSIfUserHasCTCEnrolment()

    values.filterNot(enrolmentsToRemove.contains(_))
  }

  def options()(implicit request: ServiceInfoRequest[_], appConfig: FrontendAppConfig): Seq[RadioOption] = {
    filteredRadios.map {
      value =>
        RadioOption("doYouWantToAddImportExport", value.toString)
    }
  }

  implicit val enumerable: Enumerable[DoYouWantToAddImportExport] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
