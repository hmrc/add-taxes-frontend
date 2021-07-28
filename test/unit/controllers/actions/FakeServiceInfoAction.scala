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

import controllers.ServiceInfoController
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.HmrcEnrolmentType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FakeServiceInfoAction(sipc: ServiceInfoController,
                            hcfpc: HeaderCarrierForPartialsConverter) extends ServiceInfoAction(sipc, hcfpc) {

  def apply(enrolments: HmrcEnrolmentType*): FakeServiceInfoActionWithEnrolments =
    new FakeServiceInfoActionWithEnrolments(enrolments: _*)(sipc, hcfpc)

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[ServiceInfoRequest[A]] = {
    Future.successful(ServiceInfoRequest(request, HtmlFormat.empty))
  }
}

class FakeServiceInfoActionWithEnrolments(enrolmentTypes: HmrcEnrolmentType*)
                                         (sipc: ServiceInfoController,
                                          hcfpc: HeaderCarrierForPartialsConverter) extends ServiceInfoAction(sipc, hcfpc) {
  override protected def transform[A](request: AuthenticatedRequest[A]): Future[ServiceInfoRequest[A]] = {
    val enrolments = Enrolments(enrolmentTypes.map(e => Enrolment(e.toString)).toSet)
    val requestWithEnrolments =
      AuthenticatedRequest(request.request, request.externalId, enrolments, request.affinityGroup, request.groupId, request.credId, request.confidenceLevel)

    Future.successful(ServiceInfoRequest(requestWithEnrolments, HtmlFormat.empty))
  }
}
