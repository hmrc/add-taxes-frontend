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

package controllers.deenrolment

import controllers.ControllerSpecBase
import play.api.test.Helpers._
import utils.Enrolments

class DeenrolmentProxyControllerSpec extends ControllerSpecBase {

  def controller(): DeenrolmentProxyController = {
    new DeenrolmentProxyController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction
    )
  }

  "DeenrolmentProxy Controller" must {

    val enrolments = Enrolments.values -
      (
        Enrolments.AddCis, Enrolments.PSA, Enrolments.RebatedOils, Enrolments.EPAYE,
        Enrolments.SA, Enrolments.CT, Enrolments.VAT, Enrolments.GeneralBetting,
        Enrolments.Charities, Enrolments.VATMOSS, Enrolments.RemoteGaming, Enrolments.PoolBetting,
        Enrolments.VATMOSSNonUnion, Enrolments.ATWD, Enrolments.MachineGamingDuty, Enrolments.MTDVAT
    )

    for (enrolment <- enrolments) {
      s"redirect to deenrolment management for $enrolment" in {
        val result = controller().onPageLoad(enrolment)(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(
          s"http://localhost:9555/enrolment-management-frontend/$enrolment/remove-access-tax-scheme?continue=%2Fbusiness-account")
      }
    }

    val nonEmacRedirectEnrolments = List(
      (Enrolments.AddCis, "cis/how-to-stop-cis"),
      (Enrolments.PSA, "psa/how-to-stop-psa"),
      (Enrolments.RebatedOils, "ro/how-to-stop-ro"),
      (Enrolments.EPAYE, "epaye/how-to-stop-paye"),
      (Enrolments.SA, "self-assessment/how-to-stop-sa"),
      (Enrolments.CT, "ct/how-to-stop-ct"),
      (Enrolments.VAT, "vat/how-to-stop-vat"),
      (Enrolments.GeneralBetting, "gambling/how-to-stop-gbd"),
      (Enrolments.VATMOSS, "vat/how-to-stop-vat-moss"),
      (Enrolments.VATMOSSNonUnion, "vat/how-to-stop-vat-moss-nu"),
      (Enrolments.Charities, "charities/how-to-stop-charities"),
      (Enrolments.RemoteGaming, "gambling/how-to-stop-rgd"),
      (Enrolments.PoolBetting, "gambling/how-to-stop-pbd"),
      (Enrolments.MTDVAT, "vat/how-to-stop-mtd-vat")
    )

    for ((enrolment, url) <- nonEmacRedirectEnrolments) {
      s"redirect to how to stop/leave for $enrolment" in {
        val result = controller().onPageLoad(enrolment)(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(s"/business-account/deenrol/$url")
      }
    }

    "redirect to deenrolment management for ATWD" in {
      val result = controller().onPageLoad(Enrolments.ATWD)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        "http://localhost:9555/enrolment-management-frontend/HMCE-ATWD-ORG/remove-warehouse?continue=/account"
      )
    }

    "redirect to how to stop MGD for HMRC-MGD-ORG" in {
      val result = controller().onPageLoad(Enrolments.MachineGamingDuty)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/deenrol/gambling/how-to-stop-mgd")
    }
  }
}
