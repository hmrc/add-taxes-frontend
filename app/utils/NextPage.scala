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

package utils

import controllers.other.oil.routes
import identifiers._
import models.other.importexports.ics.EORI
import models.other.importexports.emcs.DoYouHaveASEEDNumber
import models.other.importexports.ncts.HaveAnEORINumber
import models.OtherTaxes
import models.other.oil.SelectAnOilService.{RebatedOilsEnquiryService, TiedOilsEnquiryService}
import models.other.oil.{HaveYouRegisteredForRebatedOils, HaveYouRegisteredForTiedOils, SelectAnOilService}
import models.wrongcredentials.FindingYourAccount
import play.api.mvc.Call

trait NextPage[A, B] {
  def get(b: B)(implicit urlHelper: UrlHelper): Call
}

object NextPage {

  implicit val haveAnEORINumber: NextPage[HaveAnEORINumberId.type,
    models.other.importexports.ncts.HaveAnEORINumber] = {
    new NextPage[HaveAnEORINumberId.type, models.other.importexports.ncts.HaveAnEORINumber] {
      override def get(b: models.other.importexports.ncts.HaveAnEORINumber)(implicit urlHelper: UrlHelper): Call =
        b match {
          case HaveAnEORINumber.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.NewComputerisedTransitSystem))
          case HaveAnEORINumber.No => controllers.other.importexports.ncts.routes.GetEORINumberController.onPageLoad()
        }
    }
  }

  implicit val doYouHaveDAN: NextPage[DoYouHaveDANId.type,
    models.other.importexports.dan.DoYouHaveDAN] = {
    new NextPage[DoYouHaveDANId.type, models.other.importexports.dan.DoYouHaveDAN] {
      override def get(b: models.other.importexports.dan.DoYouHaveDAN)(implicit urlHelper: UrlHelper): Call =
        b match {
          case models.other.importexports.dan.DoYouHaveDAN.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.DefermentApprovalNumber))
          case models.other.importexports.dan.DoYouHaveDAN.No => controllers.other.importexports.dan.routes.RegisterDefermentApprovalNumberController.onPageLoad()
        }
    }
  }

  implicit val doYouHaveASEEDNumber: NextPage[DoYouHaveASEEDNumberId.type,
    DoYouHaveASEEDNumber] = {
    new NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber] {
      override def get(b: DoYouHaveASEEDNumber)(implicit urlHelper: UrlHelper): Call =
        b match {
          case DoYouHaveASEEDNumber.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.ExciseMovementControlSystem))
          case DoYouHaveASEEDNumber.No => controllers.other.importexports.emcs.routes.RegisterExciseMovementControlSystemController.onPageLoad()
        }
     }
  }

  implicit val economicOperatorsRegistrationAndIdentification: NextPage[EORIId.type,
    EORI] = {
    new NextPage[EORIId.type, EORI] {
      override def get(b: EORI)(implicit urlHelper: UrlHelper): Call =
        b match {
          case EORI.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.EconomicOperatorsRegistration))
          case EORI.No => controllers.other.importexports.ics.routes.RegisterEORIController.onPageLoad()
        }
     }
  }

  implicit val otherTaxes: NextPage[OtherTaxesId.type,
    OtherTaxes] = {
    new NextPage[OtherTaxesId.type, OtherTaxes] {
      override def get(b: OtherTaxes)(implicit urlHelper: UrlHelper): Call =
        b match {
          case models.OtherTaxes.AlcoholAndTobacco => Call("GET", urlHelper.businessTaxAccountLink("alcohol"))
          case models.OtherTaxes.AutomaticExchangeOfInformation => Call("GET", urlHelper.businessTaxAccountLink("aeoi"))
          case models.OtherTaxes.Charities => Call("GET", urlHelper.businessTaxAccountLink("charities"))
          case models.OtherTaxes.GamblingAndGaming => Call("GET", urlHelper.businessTaxAccountLink("gambling"))
          case models.OtherTaxes.HousingAndLand => Call("GET", urlHelper.businessTaxAccountLink("land"))
          case models.OtherTaxes.ImportsExports => Call("GET", urlHelper.businessTaxAccountLink("import-export"))
          case models.OtherTaxes.OilAndFuel => routes.SelectAnOilServiceController.onPageLoad()
          case models.OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration => Call("GET", urlHelper.fulfilmentHouse())
        }
     }
  }

  implicit val findingYourAccount: NextPage[FindingYourAccountId.type,
    FindingYourAccount] = {
    new NextPage[FindingYourAccountId.type, FindingYourAccount] {
      override def get(b: FindingYourAccount)(implicit urlHelper: UrlHelper): Call =
        b match {
          case FindingYourAccount.DontKnowPassword => Call("GET", urlHelper.governmentGatewayLostCredentialsUrl(ForgottenOptions.ForgottenPassword))
          case FindingYourAccount.DontKnowId => Call("GET", urlHelper.governmentGatewayLostCredentialsUrl(ForgottenOptions.ForgottenId))
          case FindingYourAccount.DontKnowIdOrPassword => Call("GET", urlHelper.governmentGatewayLostCredentialsUrl(ForgottenOptions.ForgottenIdAndPassword))
        }
     }
  }

  implicit val selectAnOilService: NextPage[SelectAnOilServiceId.type, SelectAnOilService] =
    new NextPage[SelectAnOilServiceId.type, SelectAnOilService] {
      override def get(b: SelectAnOilService)(implicit urlHelper: UrlHelper): Call =
        b match {
          case RebatedOilsEnquiryService => routes.HaveYouRegisteredForRebatedOilsController.onPageLoad()
          case TiedOilsEnquiryService => routes.HaveYouRegisteredForTiedOilsController.onPageLoad()
        }
    }

  implicit val haveYouRegisteredForTiedOils: NextPage[HaveYouRegisteredForTiedOilsId.type,
    HaveYouRegisteredForTiedOils] = {
    new NextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils] {
      override def get(b: HaveYouRegisteredForTiedOils)(implicit urlHelper: UrlHelper): Call =
        b match {
          case HaveYouRegisteredForTiedOils.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.TiedOils))
          case HaveYouRegisteredForTiedOils.No => routes.RegisterTiedOilsController.onPageLoad()
        }
    }
  }

  implicit val haveYouRegisteredForRebatedOils: NextPage[HaveYouRegisteredForRebatedOilsId.type,
    HaveYouRegisteredForRebatedOils] = {
    new NextPage[HaveYouRegisteredForRebatedOilsId.type, HaveYouRegisteredForRebatedOils] {
      override def get(b: HaveYouRegisteredForRebatedOils)(implicit urlHelper: UrlHelper): Call =
        b match {
          case HaveYouRegisteredForRebatedOils.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.RebatedOils))
          case HaveYouRegisteredForRebatedOils.No => routes.RegisterRebatedOilsController.onPageLoad()
        }
    }
  }
}
