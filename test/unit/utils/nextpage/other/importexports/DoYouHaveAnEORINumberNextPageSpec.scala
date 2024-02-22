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

package utils.nextpage.other.importexports

import config.featureToggles.FeatureSwitch.NewCTCEnrolmentForNCTSJourney
import models.other.importexports.DoYouHaveEORINumber
import models.requests.ServiceInfoRequest
import play.api.mvc.Call
import uk.gov.hmrc.http.InternalServerException
import utils.Enrolments.{CommonTransitConvention, NewComputerisedTransitSystem}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouHaveAnEORINumberNextPageSpec extends NextPageSpecBase {

  "icsEori" when {
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

  "nesEori" when {
    behave like nextPage(
      NextPage.nesEori,
      DoYouHaveEORINumber.Yes,
      "/business-account/add-tax/other/import-export/nes/has-eori"
    )

    behave like nextPage(
      NextPage.nesEori,
      DoYouHaveEORINumber.No,
      "/business-account/add-tax/other/import-export/nes/no-eori"
    )
  }

  "ebtiEori" when {
    behave like nextPage(
      NextPage.ebtiEori,
      DoYouHaveEORINumber.Yes,
      "http://localhost:9555/enrolment-management-frontend/HMCE-EBTI-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.ebtiEori,
      DoYouHaveEORINumber.No,
      "/business-account/add-tax/other/import-export/ebti/register"
    )
  }

  "redirectToCTCEnrolmentIfLegacyNCTSEnrolled" when {

    "user has NewComputerisedTransitSystem enrolment" when {

      "user does not have CommonTransitConvention enrolment" must {

        "return GET /customs-enrolment-services/ctc/subscribe" in {

          implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(NewComputerisedTransitSystem))

          val expectedResult = NextPage.redirectToCTCEnrolmentIfLegacyNCTSEnrolled()
          val actualResult = Call("GET", "http://localhost:6750/customs-enrolment-services/ctc/subscribe")

          expectedResult mustBe actualResult
        }
      }

      "user does have CommonTransitConvention enrolment" must {

        "throw ISE" in {

          implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(NewComputerisedTransitSystem, CommonTransitConvention))

          intercept[InternalServerException] {
            NextPage.redirectToCTCEnrolmentIfLegacyNCTSEnrolled()
          }.message mustBe "[DoYouHaveEORINumberNextPage][redirectToCTCEnrolmentIfLegacyNCTSEnrolled] user is already enrolled for CTC"

        }
      }
    }

    "user does not have NewComputerisedTransitSystem enrolment" when {

      "user does not have CommonTransitConvention enrolment" must {

        "return GET /customs-enrolment-services/ctc/subscribe" in {

          implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq())

          val expectedResult = NextPage.redirectToCTCEnrolmentIfLegacyNCTSEnrolled()
          val actualResult = Call("GET", "http://localhost:6750/customs-enrolment-services/ctc/subscribe")

          expectedResult mustBe actualResult
        }
      }

      "user does have CommonTransitConvention enrolment" must {

        "throw ISE" in {

          implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

          intercept[InternalServerException] {
            NextPage.redirectToCTCEnrolmentIfLegacyNCTSEnrolled()
          }.message mustBe "[DoYouHaveEORINumberNextPage][redirectToCTCEnrolmentIfLegacyNCTSEnrolled] user is already enrolled for CTC"

        }
      }
    }
  }

  "nctsEori" when {

    "NewCTCEnrolmentForNCTSJourney is enabled" must {

      enable(NewCTCEnrolmentForNCTSJourney)

      behave like nextPage(
        NextPage.nctsEori,
        DoYouHaveEORINumber.Yes,
        "http://localhost:6750/customs-enrolment-services/ctc/subscribe",
        Seq(enable(NewCTCEnrolmentForNCTSJourney))
      )

      behave like nextPage(
        NextPage.nctsEori,
        DoYouHaveEORINumber.No,
        "/business-account/add-tax/other/import-export/ncts/register",
        Seq(enable(NewCTCEnrolmentForNCTSJourney))
      )
    }

    "NewCTCEnrolmentForNCTSJourney is disabled" must {

      disable(NewCTCEnrolmentForNCTSJourney)

      behave like nextPage(
        NextPage.nctsEori,
        DoYouHaveEORINumber.Yes,
        "http://localhost:9555/enrolment-management-frontend/HMCE-NCTS-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
        Seq(disable(NewCTCEnrolmentForNCTSJourney))
      )

      behave like nextPage(
        NextPage.nctsEori,
        DoYouHaveEORINumber.No,
        "/business-account/add-tax/other/import-export/ncts/register",
        Seq(disable(NewCTCEnrolmentForNCTSJourney))
      )
    }
  }

}
