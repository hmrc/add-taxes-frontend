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

import config.FrontendAppConfig
import controllers.other.gambling.gbd.{routes => gbdRoutes}
import controllers.other.gambling.pbd.{routes => pbdRoutes}
import controllers.other.gambling.rgd.{routes => rgdRoutes}
import controllers.other.importexports.dan.{routes => danRoutes}
import controllers.other.importexports.ebti.{routes => ebtiRoutes}
import controllers.other.importexports.emcs.{routes => emcsRoutes}
import controllers.other.importexports.ics.{routes => icsRoutes}
import controllers.other.importexports.ncts.{routes => nctsRoutes}
import controllers.other.importexports.nes.{routes => nesRoutes}
import controllers.other.oil.routes
import controllers.sa.partnership.{routes => saPartnerRoutes}
import controllers.sa.trust.{routes => trustRoutes}
import controllers.vat.moss.uk.{routes => vatMossUkRoutes}
import identifiers._
import models.OtherTaxes
import models.other.gambling.gbd.AreYouRegisteredGTS
import models.other.importexports.dan.DoYouHaveDAN
import models.other.importexports.emcs.DoYouHaveASEEDNumber
import models.other.importexports.nes.DoYouHaveCHIEFRole
import models.other.importexports.{DoYouHaveEORINumber, DoYouWantToAddImportExport}
import models.sa.SelectSACategory
import models.sa.partnership.DoYouWantToAddPartner
import models.sa.trust.HaveYouRegisteredTrust
import models.vat.moss.uk.{OnlineVATAccount, RegisteredForVATUk}
import models.wrongcredentials.FindingYourAccount
import play.api.mvc.{Call, Request}
import utils.nextpage.employer.cis.uk.contractor.{DoesBusinessManagePAYENextPage, IsBusinessRegisteredForPAYENextPage}
import utils.nextpage.other.aeoi.HaveYouRegisteredAEOINextPage
import utils.nextpage.other.alcohol.atwd.AreYouRegisteredWarehousekeeperNextPage
import utils.nextpage.other.alcohol.awrs.SelectAlcoholSchemeNextPage
import utils.nextpage.other.charity.DoYouHaveCharityReferenceNextPage
import utils.nextpage.other.gambling.SelectGamblingOrGamingDutyNextPage
import utils.nextpage.other.gambling.mgd.DoYouHaveMGDRegistrationNextPage
import utils.nextpage.other.oil.{HaveYouRegisteredForRebatedOilsNextPage, HaveYouRegisteredForTiedOilsNextPage, SelectAnOilServiceNextPage}
import utils.nextpage.wrongcredentials.FindingYourAccountNextPage

trait NextPage[A, B] {
  def get(b: B)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call
}

object NextPage
    extends HaveYouRegisteredAEOINextPage
    with DoYouHaveCharityReferenceNextPage
    with DoYouHaveMGDRegistrationNextPage
    with AreYouRegisteredWarehousekeeperNextPage
    with SelectGamblingOrGamingDutyNextPage
    with HaveYouRegisteredForRebatedOilsNextPage
    with IsBusinessRegisteredForPAYENextPage
    with HaveYouRegisteredForTiedOilsNextPage
    with DoesBusinessManagePAYENextPage
    with SelectAnOilServiceNextPage
    with SelectAlcoholSchemeNextPage
    with FindingYourAccountNextPage {

  implicit val registeredForVATUk: NextPage[RegisteredForVATUkId.type, models.vat.moss.uk.RegisteredForVATUk] = {
    new NextPage[RegisteredForVATUkId.type, models.vat.moss.uk.RegisteredForVATUk] {
      override def get(
        b: models.vat.moss.uk.RegisteredForVATUk)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case RegisteredForVATUk.Yes => vatMossUkRoutes.OnlineVATAccountController.onPageLoad()
          case RegisteredForVATUk.No  => vatMossUkRoutes.RegisterForVATController.onPageLoad()
        }
    }
  }

  implicit val onlineVATAccount: NextPage[OnlineVATAccountId.type, models.vat.moss.uk.OnlineVATAccount] = {
    new NextPage[OnlineVATAccountId.type, models.vat.moss.uk.OnlineVATAccount] {
      override def get(
        b: models.vat.moss.uk.OnlineVATAccount)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case OnlineVATAccount.Yes => vatMossUkRoutes.AddVATMOSSController.onPageLoad()
          case OnlineVATAccount.No  => vatMossUkRoutes.AddVATFirstController.onPageLoad()
        }
    }
  }

  implicit val selectSACategory: NextPage[SelectSACategoryId.type, models.sa.SelectSACategory] = {
    new NextPage[SelectSACategoryId.type, models.sa.SelectSACategory] {
      override def get(
        b: models.sa.SelectSACategory)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case SelectSACategory.Sa          => Call(GET, appConfig.getPortalUrl("businessRegistration"))
          case SelectSACategory.Partnership => saPartnerRoutes.DoYouWantToAddPartnerController.onPageLoad()
          case SelectSACategory.Trust       => trustRoutes.HaveYouRegisteredTrustController.onPageLoad()
        }
    }
  }

  implicit val whichPensionSchemeToAdd
    : NextPage[WhichPensionSchemeToAddId.type, models.employer.pension.WhichPensionSchemeToAdd] = {
    new NextPage[WhichPensionSchemeToAddId.type, models.employer.pension.WhichPensionSchemeToAdd] {
      override def get(b: models.employer.pension.WhichPensionSchemeToAdd)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case models.employer.pension.WhichPensionSchemeToAdd.Administrators =>
            Call(GET, appConfig.getPortalUrl("pensionAdministrators"))
          case models.employer.pension.WhichPensionSchemeToAdd.Practitioners =>
            Call(GET, appConfig.getPortalUrl("pensionPractitioners"))
        }
    }
  }

  implicit val rgdGTS: NextPage[AreYouRegisteredGTSId.RGD.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.RGD.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.RemoteGaming))
          case AreYouRegisteredGTS.No  => rgdRoutes.RegisterRGDController.onPageLoad()
        }
    }
  }

  implicit val gbdGTS: NextPage[AreYouRegisteredGTSId.GBD.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.GBD.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.GeneralBetting))
          case AreYouRegisteredGTS.No  => gbdRoutes.RegisterGBDController.onPageLoad()
        }
    }
  }

  implicit val pbdGTS: NextPage[AreYouRegisteredGTSId.PBD.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.PBD.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.PoolBetting))
          case AreYouRegisteredGTS.No  => pbdRoutes.RegisterGTSFirstController.onPageLoad()
        }
    }
  }

  implicit val doYouWantToAddPartner: NextPage[DoYouWantToAddPartnerId.type, DoYouWantToAddPartner] = {
    new NextPage[DoYouWantToAddPartnerId.type, DoYouWantToAddPartner] {
      override def get(b: DoYouWantToAddPartner)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouWantToAddPartner.Yes => Call(GET, appConfig.getPublishedAssetsUrl("partnership"))
          case DoYouWantToAddPartner.No  => saPartnerRoutes.HaveYouRegisteredPartnershipController.onPageLoad()
        }
    }
  }

  implicit val haveYouRegisteredPartnership
    : NextPage[HaveYouRegisteredPartnershipId.type, models.sa.partnership.HaveYouRegisteredPartnership] = {
    new NextPage[HaveYouRegisteredPartnershipId.type, models.sa.partnership.HaveYouRegisteredPartnership] {
      override def get(b: models.sa.partnership.HaveYouRegisteredPartnership)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case models.sa.partnership.HaveYouRegisteredPartnership.Yes =>
            Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.SAPartnership))
          case models.sa.partnership.HaveYouRegisteredPartnership.No =>
            Call(GET, appConfig.getPublishedAssetsUrl("partnershipOther"))
        }
    }
  }

  implicit val haveYouRegisteredTrust: NextPage[HaveYouRegisteredTrustId.type, HaveYouRegisteredTrust] = {
    new NextPage[HaveYouRegisteredTrustId.type, HaveYouRegisteredTrust] {
      override def get(b: HaveYouRegisteredTrust)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case HaveYouRegisteredTrust.Yes => Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.RegisterTrusts))
          case HaveYouRegisteredTrust.No  => trustRoutes.RegisterTrustController.onPageLoad()
        }
    }
  }

  implicit val doYouWantToAddImportExport: NextPage[DoYouWantToAddImportExportId.type, DoYouWantToAddImportExport] = {
    new NextPage[DoYouWantToAddImportExportId.type, models.other.importexports.DoYouWantToAddImportExport] {
      override def get(b: models.other.importexports.DoYouWantToAddImportExport)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case DoYouWantToAddImportExport.EMCS => emcsRoutes.DoYouHaveASEEDNumberController.onPageLoad()
          case DoYouWantToAddImportExport.ICS  => icsRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.DDES => danRoutes.DoYouHaveDANController.onPageLoad()
          case DoYouWantToAddImportExport.NOVA => Call(GET, appConfig.getPortalUrl("novaEnrolment"))
          case DoYouWantToAddImportExport.NCTS => nctsRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.eBTI => ebtiRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.NES  => nesRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.ISD  => Call(GET, appConfig.getHmceURL("isd"))
        }
    }
  }

  implicit val doYouHaveCHIEFHasEORIRole: NextPage[DoYouHaveCHIEFRoleId.HasEORI.type, DoYouHaveCHIEFRole] = {
    new NextPage[DoYouHaveCHIEFRoleId.HasEORI.type, DoYouHaveCHIEFRole] {
      override def get(b: DoYouHaveCHIEFRole)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveCHIEFRole.Yes => Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.NewExportSystem))
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

  implicit val doYouHaveDAN: NextPage[DoYouHaveDANId.type, DoYouHaveDAN] = {
    new NextPage[DoYouHaveDANId.type, DoYouHaveDAN] {
      override def get(b: DoYouHaveDAN)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveDAN.Yes => Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.DefermentApprovalNumber))
          case DoYouHaveDAN.No  => danRoutes.RegisterDefermentApprovalNumberController.onPageLoad()
        }
    }
  }

  implicit val doYouHaveASEEDNumber: NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber] = {
    new NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber] {
      override def get(b: DoYouHaveASEEDNumber)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveASEEDNumber.Yes =>
            Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.ExciseMovementControlSystem))
          case DoYouHaveASEEDNumber.No => emcsRoutes.RegisterExciseMovementControlSystemController.onPageLoad()
        }
    }
  }

  implicit val icsEori: NextPage[DoYouHaveEORINumberId.ICS.type, DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.ICS.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes =>
            Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.EconomicOperatorsRegistration))
          case DoYouHaveEORINumber.No => icsRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val ebtiEori: NextPage[DoYouHaveEORINumberId.EBTI.type, DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.EBTI.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes =>
            Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.ElectronicBindingTariffInformation))
          case DoYouHaveEORINumber.No => ebtiRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val nctsEori: NextPage[DoYouHaveEORINumberId.NCTS.type, DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.NCTS.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes =>
            Call(GET, appConfig.emacEnrollmentsUrl(Enrolments.NewComputerisedTransitSystem))
          case DoYouHaveEORINumber.No => nctsRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val nesEori: NextPage[DoYouHaveEORINumberId.NES.type, DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.NES.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes => nesRoutes.DoYouHaveCHIEFRoleHasEORIController.onPageLoad()
          case DoYouHaveEORINumber.No  => nesRoutes.DoYouHaveCHIEFRoleNoEORIController.onPageLoad()
        }
    }
  }

  implicit val otherTaxes: NextPage[OtherTaxesId.type, OtherTaxes] = {
    new NextPage[OtherTaxesId.type, OtherTaxes] {
      override def get(b: OtherTaxes)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case models.OtherTaxes.AlcoholAndTobacco                            => Call(GET, appConfig.getBusinessAccountUrl("alcohol"))
          case models.OtherTaxes.AutomaticExchangeOfInformation               => Call(GET, appConfig.getBusinessAccountUrl("aeoi"))
          case models.OtherTaxes.Charities                                    => Call(GET, appConfig.getBusinessAccountUrl("charities"))
          case models.OtherTaxes.GamblingAndGaming                            => Call(GET, appConfig.getBusinessAccountUrl("gambling"))
          case models.OtherTaxes.HousingAndLand                               => Call(GET, appConfig.getBusinessAccountUrl("land"))
          case models.OtherTaxes.ImportsExports                               => Call(GET, appConfig.getBusinessAccountUrl("import-export"))
          case models.OtherTaxes.OilAndFuel                                   => routes.SelectAnOilServiceController.onPageLoad()
          case models.OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration => Call(GET, appConfig.fulfilmentHouse)
        }
    }
  }

  private val GET: String = "GET"
}
