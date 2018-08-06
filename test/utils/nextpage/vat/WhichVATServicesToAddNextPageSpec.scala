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
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class WhichVATServicesToAddNextPageSpec extends NextPageSpecBase {

  val affinityGroupOrganisation = Some(AffinityGroup.Organisation)
  val noEnrolments = Enrolments(Set())
  val affinityGroupIndividual = Some(AffinityGroup.Individual)
  val enrolledInVAT = Enrolments(Set(vatEnrolment))

  "whichVATServicesToAdd" when {
    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.VAT, affinityGroupOrganisation, noEnrolments),
      "http://localhost:8080/portal/business-registration/introduction?lang=eng"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.ECSales, affinityGroupOrganisation, noEnrolments),
      "/business-account/add-tax/vat/ec"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.ECSales, affinityGroupOrganisation, enrolledInVAT),
      "http://localhost:9555/enrolment-management-frontend/HMCE-ECSL-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.GIANT, affinityGroupOrganisation, noEnrolments),
      "/business-account/add-tax/vat/vat-giant"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.GIANT, affinityGroupOrganisation, enrolledInVAT),
      "/business-account/add-tax/vat/vat-giant"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.EURefunds, affinityGroupOrganisation, noEnrolments),
      "/business-account/add-tax/vat/eurefunds"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.EURefunds, affinityGroupOrganisation, enrolledInVAT),
      "http://localhost:9555/enrolment-management-frontend/HMRC-EU-REF-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.RCSL, affinityGroupOrganisation, noEnrolments),
      "/business-account/add-tax/vat/rcsl"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.RCSL, affinityGroupOrganisation, enrolledInVAT),
      "http://localhost:9555/enrolment-management-frontend/HMCE-VATRSL-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.MOSS, affinityGroupIndividual, noEnrolments),
      "/business-account/add-tax/vat/moss/newaccount"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.MOSS, affinityGroupIndividual, enrolledInVAT),
      "/business-account/add-tax/vat/moss/newaccount"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.MOSS, affinityGroupOrganisation, noEnrolments),
      "/business-account/add-tax/vat/moss"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.MOSS, affinityGroupOrganisation, enrolledInVAT),
      "/business-account/add-tax/vat/moss/zzz"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.NOVA, affinityGroupOrganisation, noEnrolments),
      "http://localhost:8080/portal/nova/normal?lang=eng"
    )
  }
}
