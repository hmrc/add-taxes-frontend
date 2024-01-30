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

package utils.nextpage.other.importexports

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.NewCTCEnrolmentForNCTSJourney
import config.featureToggles.FeatureToggleSupport
import controllers.other.importexports.ebti.{routes => ebtiRoutes}
import controllers.other.importexports.ics.{routes => icsRoutes}
import controllers.other.importexports.ncts.{routes => nctsRoutes}
import controllers.other.importexports.nes.{routes => nesRoutes}
import identifiers.DoYouHaveEORINumberId
import models.other.importexports.DoYouHaveEORINumber
import models.requests.ServiceInfoRequest
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import uk.gov.hmrc.http.InternalServerException
import utils.Enrolments.{CommonTransitConvention, NewComputerisedTransitSystem}
import utils.{Enrolments, LoggingUtil, NextPage}

trait DoYouHaveEORINumberNextPage extends LoggingUtil with FeatureToggleSupport {

  implicit val icsEori: NextPage[DoYouHaveEORINumberId.ICS.type, DoYouHaveEORINumber, Call] = {
    new NextPage[DoYouHaveEORINumberId.ICS.type, DoYouHaveEORINumber, Call] {
      override def get(b: DoYouHaveEORINumber)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.EconomicOperatorsRegistration))
          case DoYouHaveEORINumber.No => icsRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val ebtiEori: NextPage[DoYouHaveEORINumberId.EBTI.type, DoYouHaveEORINumber, Call] = {
    new NextPage[DoYouHaveEORINumberId.EBTI.type, DoYouHaveEORINumber, Call] {
      override def get(b: DoYouHaveEORINumber)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.ElectronicBindingTariffInformation))
          case DoYouHaveEORINumber.No => ebtiRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  def redirectToCTCEnrolmentIfLegacyNCTSEnrolled()(implicit request: ServiceInfoRequest[_], appConfig: FrontendAppConfig): Call = {
    (request.hasEnrolments(Seq(NewComputerisedTransitSystem)),
      request.doesNotHaveEnrolments(Seq(CommonTransitConvention))) match {
      case (hasNCTS, true) =>
        infoLog(s"[DoYouHaveEORINumberNextPage][redirectToCTCEnrolmentIfLegacyNCTSEnrolled] " +
          s"redirecting to CTC enrolment. User has legacy NCTS enrloment = $hasNCTS")
        Call("GET", appConfig.getEoriCommonComponentURL("ctc"))
      case (hasNCTS, false) =>
        errorLog(s"[DoYouHaveEORINumberNextPage][redirectToCTCEnrolmentIfLegacyNCTSEnrolled] " +
          s"user is already enrolled for CTC. User has legacy NCTS enrloment = $hasNCTS ")
        throw new InternalServerException("[DoYouHaveEORINumberNextPage][redirectToCTCEnrolmentIfLegacyNCTSEnrolled] user is already enrolled for CTC")
    }
  }

  implicit val nctsEori: NextPage[DoYouHaveEORINumberId.NCTS.type, DoYouHaveEORINumber, Call] = {
    new NextPage[DoYouHaveEORINumberId.NCTS.type, DoYouHaveEORINumber, Call] {
      override def get(b: DoYouHaveEORINumber)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes if isEnabled(NewCTCEnrolmentForNCTSJourney) =>
            redirectToCTCEnrolmentIfLegacyNCTSEnrolled()
          case DoYouHaveEORINumber.Yes =>
            Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.NewComputerisedTransitSystem))
          case DoYouHaveEORINumber.No => nctsRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val nesEori: NextPage[DoYouHaveEORINumberId.NES.type, DoYouHaveEORINumber, Call] = {
    new NextPage[DoYouHaveEORINumberId.NES.type, DoYouHaveEORINumber, Call] {
      override def get(b: DoYouHaveEORINumber)(
        implicit appConfig: FrontendAppConfig,
        request: ServiceInfoRequest[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes => nesRoutes.DoYouHaveCHIEFRoleHasEORIController.onPageLoad()
          case DoYouHaveEORINumber.No  => nesRoutes.DoYouHaveCHIEFRoleNoEORIController.onPageLoad()
        }
    }
  }
}
