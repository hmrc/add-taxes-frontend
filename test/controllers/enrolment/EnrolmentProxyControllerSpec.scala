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

package controllers.enrolment

import controllers.ControllerSpecBase
import controllers.actions.FakeServiceInfoAction
import play.api.test.Helpers._
import utils.Enrolments

class EnrolmentProxyControllerSpec extends ControllerSpecBase {

  "EnrolmentProxy Controller" must {

    val controller = new EnrolmentProxyController(frontendAppConfig, mcc, FakeAuthAction, FakeServiceInfoAction)

    "redirect to enrolment management for Individual Shared Workspace" in {
      val result = controller.onPageLoad(Enrolments.ECW)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        s"http://localhost:9555/enrolment-management-frontend/HMRC-ECW-IND/request-access-tax-scheme?continue=%2Fbusiness-account")
    }

    "redirect to enrolment management for Corporate Tax" in {
      val result = controller.onPageLoad(Enrolments.CT)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        s"http://localhost:9555/enrolment-management-frontend/IR-CT/request-access-tax-scheme?continue=%2Fbusiness-account"
      )
    }
  }
}
