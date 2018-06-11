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

package utils.nextpage.vat

import models.vat.WhichVATServicesToAdd
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}
import utils.nextpage.NextPageSpecBase

class WhichVATServicesToAddNextPageSpec extends NextPageSpecBase {

  "whichVATServicesToAdd" when {
    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.VAT, Enrolments(Set())),
      WhichVATServicesToAdd.VAT.toString,
      "http://localhost:8080/portal/business-registration/introduction?lang=eng"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.ECSales, Enrolments(Set())),
      WhichVATServicesToAdd.ECSales.toString,
      "/business-account/add-tax/vat/ec"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.EURefunds, Enrolments(Set())),
      WhichVATServicesToAdd.EURefunds.toString,
      "/business-account/add-tax/vat/eurefunds"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.RCSL, Enrolments(Set())),
      WhichVATServicesToAdd.RCSL.toString,
      "/business-account/add-tax/vat/rcsl"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.MOSS, Enrolments(Set())),
      WhichVATServicesToAdd.MOSS.toString,
      "/business-account/add-tax/vat/moss"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.NOVA, Enrolments(Set())),
      WhichVATServicesToAdd.NOVA.toString,
      "http://localhost:8080/portal/nova/normal?lang=eng"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.ECSales, Enrolments(Set(vatEnrolment))),
      WhichVATServicesToAdd.ECSales.toString,
      "http://localhost:9555/enrolment-management-frontend/HMCE-ECSL-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
      "VAT enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.EURefunds, Enrolments(Set(vatEnrolment))),
      WhichVATServicesToAdd.EURefunds.toString,
      "http://localhost:9555/enrolment-management-frontend/HMRC-EU-REF-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
      "VAT enrolment"
    )

    behave like nextPageWithEnrolments(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.RCSL, Enrolments(Set(vatEnrolment))),
      WhichVATServicesToAdd.RCSL.toString,
      "http://localhost:9555/enrolment-management-frontend/HMCE-VATRSL-ORG/request-access-tax-scheme?continue=%2Fbusiness-account",
      "VAT enrolment"
    )
  }
}
