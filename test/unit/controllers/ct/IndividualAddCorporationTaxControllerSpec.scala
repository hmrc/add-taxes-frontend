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

package controllers.ct

import controllers.ControllerSpecBase
import controllers.actions._
import models.requests.AuthenticatedRequest
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolments}
import views.html.ct.individual_add_corporation_tax

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndividualAddCorporationTaxControllerSpec extends ControllerSpecBase {

  val view: individual_add_corporation_tax = injector.instanceOf[individual_add_corporation_tax]

  def controller(fakeAuthAction: AuthAction = FakeAuthAction): IndividualAddCorporationTaxController = {
    new IndividualAddCorporationTaxController(
      frontendAppConfig,
      mcc,
      fakeAuthAction,
      FakeServiceInfoAction,
      view
    )
  }

  val application: Application = {
    new GuiceApplicationBuilder()
      .overrides(
        bind[AuthAction].to[FakeAuthActionAuthenticated]
      )
      .build()
  }

  def viewAsString(): String =
    new individual_add_corporation_tax(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

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

class FakeAuthActionAuthenticated @Inject()(bodyParsers: PlayBodyParsers)(implicit val executionContext: ExecutionContext) extends AuthAction {
  val groupId ="group-id"
  val providerId="provider-id"
  val confidenceLevel = ConfidenceLevel.L50

  override val parser: BodyParser[AnyContent] = bodyParsers.default
  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, "id", Enrolments(Set()), Some(Organisation), groupId, providerId, confidenceLevel, None))
}

