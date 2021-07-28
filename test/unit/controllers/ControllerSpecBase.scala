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

package controllers

import base.SpecBase
import controllers.actions.{FakeAuthAction, FakeDataRetrievalAction, FakeServiceInfoAction}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.{AnyContent, PlayBodyParsers}
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, Enrolments}
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global

trait ControllerSpecBase extends SpecBase {

  val cacheMapId = "id"
  val groupId ="group-id"
  val providerId="provider-id"
  val confidenceLevel = ConfidenceLevel.L50

  val parser: PlayBodyParsers = injector.instanceOf[PlayBodyParsers]
  val FakeAuthAction = new FakeAuthAction(parser)
  val FakeServiceInfoAction: FakeServiceInfoAction = {
    new FakeServiceInfoAction(
      injector.instanceOf[ServiceInfoController]
    )
  }

  def emptyCacheMap: CacheMap = CacheMap(cacheMapId, Map())

  def getEmptyCacheMap = new FakeDataRetrievalAction(Some(emptyCacheMap), global)

  def dontGetAnyData = new FakeDataRetrievalAction(None, global)

  def asDocument(s: String): Document = Jsoup.parse(s)

  def requestWithEnrolments(keys: String*): ServiceInfoRequest[AnyContent] = {
    val enrolments = Enrolments(keys.map(Enrolment(_)).toSet)
    ServiceInfoRequest[AnyContent](
      AuthenticatedRequest(FakeRequest(), "", enrolments, Some(Organisation), groupId, providerId, confidenceLevel),
      HtmlFormat.empty)
  }
}
