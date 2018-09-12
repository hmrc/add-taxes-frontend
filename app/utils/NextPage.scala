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
import play.api.mvc.Request
import utils.nextpage.OtherTaxesNextPage
import utils.nextpage.deenrolment._
import utils.nextpage.employer._
import utils.nextpage.employer.cis.IsYourBusinessInUKNextPage
import utils.nextpage.employer.cis.uk.AreYouContractorOrSubcontractorNextPage
import utils.nextpage.employer.cis.uk.subcontractor._
import utils.nextpage.employer.paye._
import utils.nextpage.employer.pension._
import utils.nextpage.other.aeoi.HaveYouRegisteredAEOINextPage
import utils.nextpage.other.alcohol.atwd.AreYouRegisteredWarehousekeeperNextPage
import utils.nextpage.other.alcohol.awrs.SelectAlcoholSchemeNextPage
import utils.nextpage.other.charity.DoYouHaveCharityReferenceNextPage
import utils.nextpage.other.ctf.AreYouApprovedCTFNextPage
import utils.nextpage.other.gambling.mgd.DoYouHaveMGDRegistrationNextPage
import utils.nextpage.other.gambling.pbd.DoYouHavePBDRegistrationNextPage
import utils.nextpage.other.gambling.rgd.DoYouHaveRGDRegistrationNextPage
import utils.nextpage.other.gambling.{AreYouRegisteredGTSNextPage, SelectGamblingOrGamingDutyNextPage}
import utils.nextpage.other.importexports.dan.DoYouHaveDANNextPage
import utils.nextpage.other.importexports.emcs.DoYouHaveASEEDNumberNextPage
import utils.nextpage.other.importexports.nes.DoYouHaveCHIEFRoleNextPage
import utils.nextpage.other.importexports.{DoYouHaveEORINumberNextPage, DoYouWantToAddImportExportNextPage}
import utils.nextpage.other.land.SelectATaxNextPage
import utils.nextpage.other.land.stampduty.StampDutyNextPage
import utils.nextpage.other.oil._
import utils.nextpage.sa._
import utils.nextpage.sa.partnership._
import utils.nextpage.sa.trust.HaveYouRegisteredTrustNextPage
import utils.nextpage.vat._
import utils.nextpage.vat.ec.RegisteredForVATECSalesNextPage
import utils.nextpage.vat.eurefunds.RegisteredForVATEURefundsNextPage
import utils.nextpage.vat.giant.WhatIsYourOrganisationNextPage
import utils.nextpage.vat.moss.iom._
import utils.nextpage.vat.moss.noneu.HaveYouRegisteredForVATMOSSNextPage
import utils.nextpage.vat.moss.uk.{OnlineVATAccountNextPage, RegisteredForVATUKNextPage}
import utils.nextpage.vat.moss.{WhereIsYourBusinessBasedNextPage, uk => vatuk}
import utils.nextpage.vat.rcsl.RegisteredForVATRCSLNextPage
import utils.nextpage.wrongcredentials.FindingYourAccountNextPage

import scala.annotation.implicitNotFound

@implicitNotFound(
  "Could not find NextPage implicit value for ${A} and ${B}. Check you have created one, the types match and extended NextPage with it")
trait NextPage[A, B, C] {
  def get(b: B)(implicit appConfig: FrontendAppConfig, request: Request[_]): C
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
    with FindingYourAccountNextPage
    with RegisteredForVATUKNextPage
    with OtherTaxesNextPage
    with OnlineVATAccountNextPage
    with SelectSACategoryNextPage
    with DoYouHaveEORINumberNextPage
    with AreYouRegisteredGTSNextPage
    with DoYouWantToAddPartnerNextPage
    with DoYouHaveASEEDNumberNextPage
    with HaveYouRegisteredPartnershipNextPage
    with DoYouHaveDANNextPage
    with HaveYouRegisteredTrustNextPage
    with DoYouHaveCHIEFRoleNextPage
    with DoYouWantToAddImportExportNextPage
    with WhereIsYourBusinessBasedNextPage
    with HaveYouRegisteredForVATMOSSNextPage
    with AlreadyRegisteredForVATMossNextPage
    with RegisteredForVATNextPage
    with WhatEmployerTaxDoYouWantToAddNextPage
    with AreYouContractorOrSubcontractorNextPage
    with IsYourBusinessInUKNextPage
    with RegisteredForVATECSalesNextPage
    with RegisteredForVATEURefundsNextPage
    with RegisteredForVATRCSLNextPage
    with WhichVATServicesToAddNextPage
    with DoYouWantToBePaidNetOrGrossNextPage
    with WasTurnoverMoreAfterVATNextPage
    with WhatTypeOfSubcontractorNextPage
    with StampDutyNextPage
    with SelectATaxNextPage
    with DoYouHavePBDRegistrationNextPage
    with DoYouHaveRGDRegistrationNextPage
    with DoYouWantToLeaveCISNextPage
    with DoYouNeedToStopRONextPage
    with DoYouNeedToStopEPAYENextPage
    with DoYouNeedToStopPSANextPage
    with HaveYouStoppedSelfEmploymentNextPage
    with AreYouApprovedCTFNextPage
    with WhatIsYourOrganisationNextPage
    with DoYouNeedToStopGBDNextPage
    with StopCorporationTaxNextPage
    with DoYouNeedToCancelVATNextPage
    with StopFilingSelfAssessmentNextPage
    with DoYouNeedToStopRGDNextPage
    with DoYouNeedToCloseCharityNextPage
    with DoYouNeedToLeaveVATMOSSNextPage
    with DoYouNeedToStopPBDNextPage
    with DoYouNeedToStopVatMossNUNextPage
    with DoYouNeedToStopMGDNextPage
    with vatuk.AlreadyRegisteredForVATMossNextPage
    with DoYouHaveVATRegNumberNextPage
    with EnterSAUTRNextPage
    with DoYouHaveSAUTRNextPage
    with DoYouHaveActivationTokenNextPage
    with DoYouHavePractitionerIDNextPage
    with DoesBusinessHave1To9DirectorsNextPage
    with DoesYourPartnershipHave2To10PartnersNextPage
    with DoesBusinessHaveDirectorsOrPartnersNextPage
    with DoYouHavePAYEReferenceNextPage
    with RegisterForVATOnlineNextPage
