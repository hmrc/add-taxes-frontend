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

import base.SpecBase
import models.other.importexports.{DoYouHaveEORINumber, DoYouWantToAddImportExport}
import models.OtherTaxes
import models.other.importexports.dan.DoYouHaveDAN
import models.other.importexports.emcs.DoYouHaveASEEDNumber
import models.other.importexports.nes.DoYouHaveCHIEFRole
import models.other.oil.SelectAnOilService.{RebatedOilsEnquiryService, TiedOilsEnquiryService}
import models.other.oil.{HaveYouRegisteredForRebatedOils, HaveYouRegisteredForTiedOils}
import models.wrongcredentials.FindingYourAccount


class NextPageSpec extends SpecBase {

  implicit val urlHelper = new UrlHelper(frontendAppConfig)


  def nextPage[A, B](np: NextPage[A, B], userSelection: B, urlRedirect: String): Unit = {
    s"$userSelection is selected" should {
      s"redirect to $urlRedirect" in {
        val result = np.get(userSelection)
        result.url mustBe urlRedirect
      }
    }
  }

  "SelectAnOilService" when {
    behave like nextPage(NextPage.selectAnOilService, TiedOilsEnquiryService, "/business-account/add-tax/other/oil/tied")
    behave like nextPage(NextPage.selectAnOilService, RebatedOilsEnquiryService, "/business-account/add-tax/other/oil/rebated")
  }

  "HaveYouRegisteredForTiedOils" when {
    behave like nextPage(
      NextPage.haveYouRegisteredForTiedOils,
      HaveYouRegisteredForTiedOils.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMCE-TO/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.haveYouRegisteredForTiedOils,
      HaveYouRegisteredForTiedOils.No,
      "/business-account/add-tax/other/oil/tied/register"
    )
  }

  "OtherTaxes" when {
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.AlcoholAndTobacco,
      "http://localhost:9020/business-account/add-tax/other/alcohol"
    )

    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.AutomaticExchangeOfInformation,
      "http://localhost:9020/business-account/add-tax/other/aeoi"
    )
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.Charities,
      "http://localhost:9020/business-account/add-tax/other/charities"
    )
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.GamblingAndGaming,
      "http://localhost:9020/business-account/add-tax/other/gambling"
    )
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.HousingAndLand,
      "http://localhost:9020/business-account/add-tax/other/land"
    )
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.ImportsExports,
      "http://localhost:9020/business-account/add-tax/other/import-export"
    )
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.OilAndFuel,
      "/business-account/add-tax/other/oil"
    )
    behave like nextPage(
      NextPage.otherTaxes,
      OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration,
      "http://localhost:1118/fhdds"
    )
  }


  "HaveYouRegisteredForRebatedOils" when {
    behave like nextPage(
      NextPage.haveYouRegisteredForRebatedOils,
      HaveYouRegisteredForRebatedOils.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMCE-RO/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.haveYouRegisteredForRebatedOils,
      HaveYouRegisteredForRebatedOils.No,
      "/business-account/add-tax/other/oil/rebated/register"
    )
  }

  "FindingYourAccountFormProvider" when {
    def governmentGatewayUrlGenerator(forgottenOption: String): String =
      s"http://localhost:9898/government-gateway-lost-credentials-frontend/" +
        s"choose-your-account?continue=%2Fbusiness-account&origin=business-tax-account&forgottenOption=$forgottenOption"

    behave like nextPage(NextPage.findingYourAccount, FindingYourAccount.DontKnowId, governmentGatewayUrlGenerator("userId"))

    behave like nextPage(NextPage.findingYourAccount, FindingYourAccount.DontKnowPassword,
      governmentGatewayUrlGenerator("password"))

    behave like nextPage(NextPage.findingYourAccount, FindingYourAccount.DontKnowIdOrPassword,
      governmentGatewayUrlGenerator("UserIdAndPassword"))
  }

  "EconomicOperatorsRegistrationAndIdentification" when {
    behave like nextPage(
      NextPage.icsEori,
      DoYouHaveEORINumber.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMRC-ICS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.icsEori,
      DoYouHaveEORINumber.No,
      "/business-account/add-tax/other/import-export/ics/register"
    )
  }

  "DoYouHaveSEEDNumber" when {
    behave like nextPage(
      NextPage.doYouHaveASEEDNumber,
      DoYouHaveASEEDNumber.No,
      "/business-account/add-tax/other/import-export/emcs/register"
    )

    behave like nextPage(
      NextPage.doYouHaveASEEDNumber,
      DoYouHaveASEEDNumber.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMRC-EMCS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }

  "DoYouHaveDAN" when {
    behave like nextPage(
      NextPage.doYouHaveDAN,
      DoYouHaveDAN.No,
      "/business-account/add-tax/other/import-export/ddes/register"
    )

    behave like nextPage(
      NextPage.doYouHaveDAN,
      DoYouHaveDAN.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMCE-DDES/request-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }

  "DoYouHaveCHIEFRole" when {
    behave like nextPage(
      NextPage.doYouHaveCHIEFRole,
      DoYouHaveCHIEFRole.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMCE-NES/request-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }

  "nesEori" when {
    behave like nextPage(
      NextPage.nesEori,
      DoYouHaveEORINumber.Yes,
      "/business-account/add-tax/other/import-export/nes/has-eori"
    )
  }

  "DoYouWantToAddImportExport" when {
    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.EMCS,
      "/business-account/add-tax/other/import-export/emcs"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.ICS,
      "/business-account/add-tax/other/import-export/ics"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.DDES,
      "/business-account/add-tax/other/import-export/ddes"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.NOVA,
      "http://localhost:8080/portal/nova/normal"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.NCTS,
      "/business-account/add-tax/other/import-export/ncts"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.eBTI,
      "/business-account/add-tax/other/import-export/ebti"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.NES,
      "/business-account/add-tax/other/import-export/nes"
    )

    behave like nextPage(
      NextPage.doYouWantToAddImportExport,
      DoYouWantToAddImportExport.Intrastat,
      "https://secure.hmce.gov.uk/ecom/is2/static/is2.html"
    )
  }
}
