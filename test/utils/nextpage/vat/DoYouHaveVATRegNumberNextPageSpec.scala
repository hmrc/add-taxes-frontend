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
import models.vat.DoYouHaveVATRegNumber
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.{Enrolments, NextPage}
import utils.nextpage.NextPageSpecBase

class DoYouHaveVATRegNumberNextPageSpec extends NextPageSpecBase with MockitoSugar {

  val mockConfig: FrontendAppConfig = mock[FrontendAppConfig]
  override implicit def frontendAppConfig: FrontendAppConfig = mockConfig

  val affinityGroupOrganisation = Some(AffinityGroup.Organisation)
  val affinityGroupIndividual = Some(AffinityGroup.Individual)

  private trait FeatureEnabled {
    when(frontendAppConfig.mtdVatSignUpJourneyEnabled).thenReturn(true)
  }

  private trait FeatureDisabled {
    when(frontendAppConfig.mtdVatSignUpJourneyEnabled).thenReturn(false)
    when(frontendAppConfig.emacEnrollmentsUrl(Enrolments.VAT)).thenReturn(
      "http://localhost:9555/enrolment-management-frontend/HMCE-VATDEC-ORG/" +
        "request-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }

  "doYouHaveVATRegNumber" when {

    "the mtd-vat-signup feature is turned on" should {

      val nextPage = NextPage.doYouHaveVATRegNumber
      val userSelection = (DoYouHaveVATRegNumber.Yes, None)
      val redirectLocation = "/business-account/add-tax/vat/what-is-your-vat-number"

      s"redirect to $redirectLocation" in new FeatureEnabled {
        val result: Call = nextPage.get(userSelection)
        result.url mustBe redirectLocation
      }
    }

    "the mtd-vat-signup feature is turned off" should {

      val nextPage = NextPage.doYouHaveVATRegNumber
      val userSelection = (DoYouHaveVATRegNumber.Yes, None)
      val redirectLocation = "http://localhost:9555/enrolment-management-frontend/HMCE-VATDEC-ORG/" +
        "request-access-tax-scheme?continue=%2Fbusiness-account"

      s"redirect to $redirectLocation" in new FeatureDisabled {
        val result: Call = nextPage.get(userSelection)
        result.url mustBe redirectLocation
      }
    }

    behave like nextPage(
      NextPage.doYouHaveVATRegNumber,
      (DoYouHaveVATRegNumber.No, affinityGroupOrganisation),
      "/business-account/add-tax/vat/register-online"
    )

    behave like nextPage(
      NextPage.doYouHaveVATRegNumber,
      (DoYouHaveVATRegNumber.No, affinityGroupIndividual),
      "/business-account/add-tax/vat/registered/no"
    )
  }
}
