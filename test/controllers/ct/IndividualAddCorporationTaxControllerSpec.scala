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

import controllers.ControllerSpecBase
import controllers.actions.{FakeServiceInfoAction, _}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.Enrolments
import play.api.inject.bind
import play.twirl.api.HtmlFormat
import views.html.ct.individual_add_corporation_tax

import scala.concurrent.Future

class IndividualAddCorporationTaxControllerSpec extends ControllerSpecBase {

  def controller(
    dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
    fakeAuthAction: AuthAction = FakeAuthAction) =
    new IndividualAddCorporationTaxController(frontendAppConfig, messagesApi, fakeAuthAction, FakeServiceInfoAction)

  val application = new GuiceApplicationBuilder()
    .overrides(
      bind[AuthAction].to[FakeAuthActionAuthenticated],
      bind[ServiceInfoAction].to[FakeServiceInfoActionEmpty]
    )
    .build()

  def viewAsString() =
    individual_add_corporation_tax(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "IndividualAddCorporationTaxControllerSpec" should {
    "return OK when called through route" in {
      val requestF = FakeRequest(GET, "/business-account/add-tax/corporation-tax").withHeaders("host" -> "localhost")
      val request = route(application, requestF).get
      status(request) mustBe OK
    }

    "return OK and the correct view for a GET" in {
      val request = controller().onPageLoad()(fakeRequest)
      contentAsString(request) mustBe viewAsString()
      status(request) mustBe OK
    }
  }

}

class FakeAuthActionAuthenticated extends AuthAction {
  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, "id", Enrolments(Set()), Some(Organisation)))
}

class FakeServiceInfoActionEmpty extends ServiceInfoAction {
  override protected def transform[A](request: AuthenticatedRequest[A]): Future[ServiceInfoRequest[A]] = {
    implicit val r: Request[A] = request
    Future.successful(ServiceInfoRequest(request, HtmlFormat.empty))
  }
}