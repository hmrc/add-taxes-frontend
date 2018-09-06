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

package utils.nextpage.employer

import models.employer.WhatEmployerTaxDoYouWantToAdd
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class WhatEmployerTaxDoYouWantToAddNextPageSpec extends NextPageSpecBase {

  val nextPageEPAYENoEnrolment = (WhatEmployerTaxDoYouWantToAdd.EPAYE, Enrolments(Set()))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageEPAYENoEnrolment,
    WhatEmployerTaxDoYouWantToAdd.EPAYE.toString,
    "/business-account/add-tax/employer/have-paye-ref",
    "no enrolments"
  )

  val nextPageCISWithEnrolment = (WhatEmployerTaxDoYouWantToAdd.CIS, Enrolments(Set(epayeEnrolment)))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageCISWithEnrolment,
    WhatEmployerTaxDoYouWantToAdd.CIS.toString,
    "/business-account/add-tax/employer/cis",
    "EPAYE enrolments"
  )

  val nextPageCISNoEnrolment = (WhatEmployerTaxDoYouWantToAdd.CIS, Enrolments(Set()))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageCISNoEnrolment,
    WhatEmployerTaxDoYouWantToAdd.CIS.toString,
    "/business-account/add-tax/employer/cis",
    "no enrolments"
  )

  val nextPagePensionNoEnrolment = (WhatEmployerTaxDoYouWantToAdd.PS, Enrolments(Set()))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPagePensionNoEnrolment,
    WhatEmployerTaxDoYouWantToAdd.PS.toString,
    "/business-account/add-tax/employer/pension/practitioner-ID",
    "no enrolments"
  )

  val nextPageERSWithEnrolment = (WhatEmployerTaxDoYouWantToAdd.ERS, Enrolments(Set(epayeEnrolment)))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageERSWithEnrolment,
    WhatEmployerTaxDoYouWantToAdd.ERS.toString,
    "http://localhost:8080/portal/ers/org///add-scheme?lang=eng",
    "EPAYE enrolments"
  )

  val nextPageERSNoEnrolment = (WhatEmployerTaxDoYouWantToAdd.ERS, Enrolments(Set()))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageERSNoEnrolment,
    WhatEmployerTaxDoYouWantToAdd.ERS.toString,
    "/business-account/add-tax/employer/ers",
    "no enrolments"
  )

  val nextPageEIAWithEnrolment = (WhatEmployerTaxDoYouWantToAdd.EIA, Enrolments(Set(epayeEnrolment)))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageEIAWithEnrolment,
    WhatEmployerTaxDoYouWantToAdd.EIA.toString,
    "/employment-intermediary-report/not-enrolled",
    "EPAYE enrolments"
  )

  val nextPageEIANoEnrolment = (WhatEmployerTaxDoYouWantToAdd.EIA, Enrolments(Set()))

  behave like nextPageWithEnrolments(
    NextPage.whatEmployerTaxDoYouWantToAdd,
    nextPageEIANoEnrolment,
    WhatEmployerTaxDoYouWantToAdd.EIA.toString,
    "/business-account/add-tax/employer/intermediaries",
    "no enrolments"
  )
}
