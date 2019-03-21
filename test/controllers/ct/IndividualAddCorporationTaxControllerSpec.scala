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

package controllers.ct

import controllers.{ControllerSpecBase}
import controllers.actions.{AuthAction, DataRetrievalAction, FakeAuthAction, FakeServiceInfoAction}
import models.requests.AuthenticatedRequest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.Enrolments
import play.api.inject.bind

import scala.concurrent.Future

class FakeAuthActionAuthenticated extends AuthAction {
  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, "id", Enrolments(Set()), Some(Organisation)))
}

class IndividualAddCorporationTaxControllerSpec extends ControllerSpecBase {

  def controller(
    dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
    fakeAuthAction: AuthAction = FakeAuthAction) =
    new IndividualAddCorporationTaxController(frontendAppConfig, messagesApi, fakeAuthAction, FakeServiceInfoAction)

  val application = new GuiceApplicationBuilder()
    .overrides(
      bind[AuthAction].to[FakeAuthActionAuthenticated]
    )
    .build()

  "IndividualAddCorporationTaxControllerSpec" should {
    "return OK when called through route" in {
      val requestF = FakeRequest(GET, "/business-account/add-tax/corporation-tax").withHeaders("host" -> "localhost")
      val request = route(application, requestF).get
      status(request) mustBe OK
    }

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)
      status(result) mustBe OK
    }
  }

}
