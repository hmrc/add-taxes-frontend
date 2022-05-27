/*
 * Copyright 2020 HM Revenue & Customs
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
import config.featureToggles.FeatureToggleSupport
import models.vat.DoYouHaveVATRegNumber
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.nextpage.NextPageSpecBase
import utils.NextPage

class DoYouHaveVATRegNumberNextPageSpec
    extends NextPageSpecBase
    with MockitoSugar
    with FeatureToggleSupport
    with BeforeAndAfterEach {

  val mockConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]
  override implicit def frontendAppConfig: FrontendAppConfig = mockConfig

  val affinityGroupOrganisation = Some(AffinityGroup.Organisation)
  val affinityGroupIndividual = Some(AffinityGroup.Individual)


  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  behave like nextPage(
    NextPage.doYouHaveVATRegNumber,
    (DoYouHaveVATRegNumber.No, affinityGroupOrganisation),
    frontendAppConfig.vatRegHandoff
  )

  behave like nextPage(
    NextPage.doYouHaveVATRegNumber,
    (DoYouHaveVATRegNumber.No, affinityGroupIndividual),
    frontendAppConfig.vatRegHandoff
  )

  "doYouHaveVATRegNumber" when {

    "mtd-vat-signup" should {

      val nextPage = NextPage.doYouHaveVATRegNumber
      val userSelection = (DoYouHaveVATRegNumber.Yes, None)
      val redirectLocation = "/business-account/add-tax/vat/what-is-your-vat-number"

      s"redirect to $redirectLocation" in {
        val result: Call = nextPage.get(userSelection)
        result.url mustBe redirectLocation
      }
    }

    "the newVatJourney" should {

      "redirect the user to start the new vat journey" in {

        val nextPage = NextPage.doYouHaveVATRegNumber

        val result: Call = nextPage.get((DoYouHaveVATRegNumber.No, None))

        result.url mustBe frontendAppConfig.vatRegHandoff
      }
    }
  }
}
