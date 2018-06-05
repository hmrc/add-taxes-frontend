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
import play.api.mvc.{Call, Request}
import utils.nextpage.OtherTaxesNextPage
import utils.nextpage.employer.{DoesBusinessManagePAYENextPage, IsBusinessRegisteredForPAYENextPage}
import utils.nextpage.employer.pension.WhichPensionSchemeToAddNextPage
import utils.nextpage.other.aeoi.HaveYouRegisteredAEOINextPage
import utils.nextpage.other.alcohol.atwd.AreYouRegisteredWarehousekeeperNextPage
import utils.nextpage.other.alcohol.awrs.SelectAlcoholSchemeNextPage
import utils.nextpage.other.charity.DoYouHaveCharityReferenceNextPage
import utils.nextpage.other.gambling.mgd.DoYouHaveMGDRegistrationNextPage
import utils.nextpage.other.gambling.{AreYouRegisteredGTSNextPage, SelectGamblingOrGamingDutyNextPage}
import utils.nextpage.other.importexports.{DoYouHaveEORINumberNextPage, DoYouWantToAddImportExportNextPage}
import utils.nextpage.other.importexports.dan.DoYouHaveDANNextPage
import utils.nextpage.other.importexports.emcs.DoYouHaveASEEDNumberNextPage
import utils.nextpage.other.importexports.nes.DoYouHaveCHIEFRoleNextPage
import utils.nextpage.other.oil._
import utils.nextpage.sa.SelectSACategoryNextPage
import utils.nextpage.sa.partnership._
import utils.nextpage.sa.trust.HaveYouRegisteredTrustNextPage
import utils.nextpage.vat.moss.iom._
import utils.nextpage.vat.moss.uk.{OnlineVATAccountNextPage, RegisteredForVATUKNextPage}
import utils.nextpage.wrongcredentials.FindingYourAccountNextPage
import utils.nextpage.vat.ec.RegisteredForVATECSalesNextPage
import utils.nextpage.vat.eurefunds.RegisteredForVATEURefundsNextPage
import utils.nextpage.vat.moss.noneu.HaveYouRegisteredForVATMOSSNextPage
import utils.nextpage.vat.WhichVATServicesToAddNextPage
import utils.nextpage.vat.rcsl.RegisteredForVATRCSLNextPage
import utils.nextpage.vat.moss.WhereIsYourBusinessBasedNextPage
import utils.nextpage.employer.cis.uk.subcontractor._

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
    with FindingYourAccountNextPage
    with RegisteredForVATUKNextPage
    with OtherTaxesNextPage
    with OnlineVATAccountNextPage
    with SelectSACategoryNextPage
    with DoYouHaveEORINumberNextPage
    with WhichPensionSchemeToAddNextPage
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
    with RegisteredForVATECSalesNextPage
    with RegisteredForVATEURefundsNextPage
    with RegisteredForVATRCSLNextPage
    with WhichVATServicesToAddNextPage
    with DoYouWantToBePaidNetOrGrossNextPage
    with WasTurnoverMoreAfterVATNextPage
    with WhatTypeOfSubcontractorNextPage
