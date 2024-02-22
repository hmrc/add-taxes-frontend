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

package controllers.actions

import base.SpecBase
import controllers.actions.AuthActionSpec.AuthUtil
import controllers.routes
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{Action, AnyContent, PlayBodyParsers}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Credentials, ~}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

object AuthActionSpec {

  implicit class AuthUtil[A](val input: A) extends AnyVal {
    def ~[B](input2: B): ~[A, B] = new ~(input, input2)
  }

}

class AuthActionSpec extends SpecBase with MockitoSugar {

  val mockAuthConnector = mock[AuthConnector]

  val parser: PlayBodyParsers = injector.instanceOf[PlayBodyParsers]

  class Harness(authAction: AuthAction) extends FrontendController(mcc) {
    def onPageLoad(): Action[AnyContent] = authAction { _ => Ok }
  }

  type RetrievalType = Option[String] ~ Enrolments ~ Option[AffinityGroup] ~ Option[String] ~ Option[Credentials] ~ ConfidenceLevel ~ Option[String]

  def retrievals(externaId: Option[String] = Some("externalId"),
                 enrolments: Enrolments = Enrolments(Set.empty),
                 affinityGroup: Option[AffinityGroup] = Some(Organisation),
                 groupId: Option[String] = Some("groupId"),
                 creds: Option[Credentials] = Some(Credentials("foo", "bar")),
                 confidenceLevel: ConfidenceLevel = ConfidenceLevel.L50,
                 nino: Option[String] = None): Harness = {

    when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(
      Future.successful(externaId ~ enrolments ~ affinityGroup ~ groupId ~ creds ~ confidenceLevel ~ nino)
    )

    val authAction =
      new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)

    new Harness(authAction)
  }

  "Auth Action" when {

    "the user hasn't logged in" must {
      "redirect the user to log in " in {

        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(MissingBearerToken()))

        val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {

        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(BearerTokenExpired()))

        val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {
        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(InsufficientEnrolments()))

        val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {
        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(InsufficientConfidenceLevel()))

        val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {
        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(UnsupportedAuthProvider()))

        val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {
        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(UnsupportedAffinityGroup()))

        val authAction =
          new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {
        when(mockAuthConnector.authorise[RetrievalType](any(), any())(any(), any())).thenReturn(Future.failed(UnsupportedCredentialRole()))

        val authAction = new AuthActionImpl(mockAuthConnector, frontendAppConfig, parser)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad.url)
      }
    }

    "the user has no groupId" must {
      "redirect the user to the verified user error page" in {

        val controller = retrievals(groupId = None)
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.VerifiedUserController.onPageLoad.url)
      }
    }

    "all retrievals returned from auth" must {
      "return 200" in {

        val controller = retrievals()
        val result = controller.onPageLoad()(fakeRequest)
        status(result) mustBe OK
      }
    }

  }
}
