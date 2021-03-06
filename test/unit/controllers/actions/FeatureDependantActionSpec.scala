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

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.FakeRequest
import playconfig.featuretoggle._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FeatureDependantActionSpec extends WordSpec with MustMatchers with FeatureToggleSupport with GuiceOneAppPerSuite {

  val testFeatureDependantAction: FeatureDependantAction = new FeatureDependantAction(app.injector.instanceOf[FeatureConfig], global)

  val testRequest: Request[AnyContent] = FakeRequest()
  val testResultBody = "testBody"
  val testBlock: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok(testResultBody))

}
