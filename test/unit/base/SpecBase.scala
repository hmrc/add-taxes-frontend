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

import config.FrontendAppConfig
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.Messages
import play.api.inject.Injector
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.govukfrontend.views.html.components.FormWithCSRF
import views.html.components.conditional_radio
import views.html.main_template

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite {

  def injector: Injector = app.injector

  val formWithCSRF: FormWithCSRF = injector.instanceOf[FormWithCSRF]
  val mainTemplate: main_template = injector.instanceOf[main_template]
  val conditionalRadio: conditional_radio = injector.instanceOf[conditional_radio]

  implicit def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit def mcc: MessagesControllerComponents = injector.instanceOf[MessagesControllerComponents]

  def fakeRequest: FakeRequest[AnyContent] = FakeRequest("", "")

  def messages: Messages = mcc.messagesApi.preferred(fakeRequest)

  val cacheMapId = "id"
  val groupId ="group-id"
  val providerId="provider-id"
  val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L50
}
