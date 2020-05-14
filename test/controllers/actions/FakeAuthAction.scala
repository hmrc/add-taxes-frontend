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

import models.requests.AuthenticatedRequest
import play.api.mvc.{AnyContent, BodyParser, PlayBodyParsers, Request, Result}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core.Enrolments

import scala.concurrent.{ExecutionContext, Future}

class FakeAuthAction(bodyParsers: PlayBodyParsers)(implicit val executionContext: ExecutionContext) extends AuthAction {
  val parser: BodyParser[AnyContent] = bodyParsers.default

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, "id", Enrolments(Set()), Some(Organisation)))
}

class FakeAuthActionIndividual(bodyParsers: PlayBodyParsers)(implicit val executionContext: ExecutionContext) extends AuthAction {
  val parser: BodyParser[AnyContent] = bodyParsers.default

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, "id", Enrolments(Set()), Some(Individual)))
}

class FakeAuthActionAgent(bodyParsers: PlayBodyParsers)(implicit val executionContext: ExecutionContext) extends AuthAction {
  val parser: BodyParser[AnyContent] = bodyParsers.default

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    block(AuthenticatedRequest(request, "id", Enrolments(Set()), Some(Agent)))
}
