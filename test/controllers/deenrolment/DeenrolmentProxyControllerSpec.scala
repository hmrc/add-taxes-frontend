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

package controllers.deenrolment

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import utils.Enrolments

class DeenrolmentProxyControllerSpec extends ControllerSpecBase {

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new DeenrolmentProxyController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction)

  "DeenrolmentProxy Controller" must {

    for (enrolment <- Enrolments.values.filterNot(_ == Enrolments.AddCis)) {
      s"redirect to deenrolment management for $enrolment" in {
        val result = controller().onPageLoad(enrolment)(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(
          s"http://localhost:9555/enrolment-management-frontend/$enrolment/remove-access-tax-scheme?continue=%2Fbusiness-account")
      }
    }

    s"redirect to how to stop cis for HMRC-CIS-ORG" in {
      val result = controller().onPageLoad(Enrolments.AddCis)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/cis/how-to-stop-cis")
    }

  }
}
