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

package base

import akka.stream.Materializer
import config.FrontendAppConfig
import config.featureToggles.FeatureToggleSupport
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.Application
import play.api.i18n.Messages
import play.api.inject.Injector
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request}
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, Enrolments}
import uk.gov.hmrc.govukfrontend.views.html.components.FormWithCSRF
import views.html.components.conditional_radio
import views.html.main_template
import utils.{Enrolments => AddTaxesEnrolments}


trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with FeatureToggleSupport with BeforeAndAfterAll {

  val injector: Injector = app.injector
  implicit def materializer(implicit app: Application): Materializer = app.materializer

  val formWithCSRF: FormWithCSRF = injector.instanceOf[FormWithCSRF]
  val mainTemplate: main_template = injector.instanceOf[main_template]
  val conditionalRadio: conditional_radio = injector.instanceOf[conditional_radio]

  implicit def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit def mcc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  def fakeRequest: FakeRequest[AnyContent] = FakeRequest("", "")

  def reqWithEnrolments(enrolments: Seq[AddTaxesEnrolments], request: Request[AnyContent] = fakeRequest): ServiceInfoRequest[_] = {
    val authEnrolments: Enrolments = Enrolments(enrolments.map(_.toAuthEnrolment).toSet)
    ServiceInfoRequest[AnyContent](
      AuthenticatedRequest(request, "", authEnrolments, Some(Organisation), groupId, providerId, confidenceLevel, None),
      HtmlFormat.empty)
  }

  def messages: Messages = mcc.messagesApi.preferred(fakeRequest)

  val cacheMapId = "id"
  val groupId ="group-id"
  val providerId="provider-id"
  val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L50

  override def afterAll(): Unit = {
    resetAll()
  }
}
