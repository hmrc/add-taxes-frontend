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

import config.featureToggles.FeatureToggleSupport
import models.vat.WhichVATServicesToAdd
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolments}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class WhichVATServicesToAddNextPageSpec extends NextPageSpecBase with FeatureToggleSupport {

  val affinityGroupOrganisation: Option[AffinityGroup.Organisation.type] = Some(AffinityGroup.Organisation)
  val noEnrolments: Enrolments = Enrolments(Set())
  val affinityGroupIndividual: Option[AffinityGroup.Individual.type] = Some(AffinityGroup.Individual)
  val enrolledInVAT = Enrolments(Set(vatEnrolment))
  val redirectUrl = "http://localhost:8081/external-entry"

  "whichVATServicesToAdd" when {
    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.VAT, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "/business-account/add-tax/vat/do-you-have-a-vat-number"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.ECSales, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "/business-account/add-tax/vat/ec"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.ECSales, affinityGroupOrganisation, enrolledInVAT, redirectUrl),
      "http://localhost:9555/enrolment-management-frontend/HMCE-ECSL-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.GIANT, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "/business-account/add-tax/vat/vat-giant"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.GIANT, affinityGroupIndividual, noEnrolments, redirectUrl),
      "/business-account/add-tax/vat/giant/newaccount"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.EURefunds, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "/business-account/add-tax/vat/eurefunds"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.EURefunds, affinityGroupOrganisation, enrolledInVAT, redirectUrl),
      "http://localhost:9555/enrolment-management-frontend/HMRC-EU-REF-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.RCSL, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "/business-account/add-tax/vat/rcsl"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.RCSL, affinityGroupOrganisation, enrolledInVAT, redirectUrl),
      "http://localhost:9555/enrolment-management-frontend/HMCE-VATRSL-ORG/request-access-tax-scheme?continue=%2Fbusiness-account"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.NOVA, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "http://localhost:8081/portal/nova/normal?lang=eng"
    )

    behave like nextPage(
      NextPage.whichVATServicesToAdd,
      (WhichVATServicesToAdd.VATOSS, affinityGroupOrganisation, noEnrolments, redirectUrl),
      "http://localhost:8081/external-entry"
    )
  }
}
