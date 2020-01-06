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
import play.api.mvc.{ActionFilter, AnyContent, Request, Result}
import play.api.test.FakeRequest
import playconfig.featuretoggle._
import play.api.test.Helpers._
import play.api.mvc.Results._
import uk.gov.hmrc.http.NotFoundException

import scala.concurrent.Future

class FeatureDependantActionSpec extends WordSpec with MustMatchers with FeatureToggleSupport with GuiceOneAppPerSuite {

  val testFeature: Feature = NewVatJourney
  val testFeatureDependantAction: FeatureDependantAction = new FeatureDependantAction(
    app.injector.instanceOf[FeatureConfig])
  val testAction: ActionFilter[Request] = testFeatureDependantAction.permitFor(testFeature)

  val testRequest: Request[AnyContent] = FakeRequest()
  val testResultBody = "testBody"
  val testBlock: Request[AnyContent] => Future[Result] = _ => Future.successful(Ok(testResultBody))

  "Feature is disabled return not found" in {
    disable(testFeature)

    val exceptionMessage = intercept[NotFoundException] {
      val result: Future[Result] = testAction.invokeBlock[AnyContent](testRequest, testBlock)
      await(result)
    }

    exceptionMessage.toString must include("The page is not enabled")
  }

  "Feature is enabled execute the block" in {
    enable(testFeature)

    val result: Future[Result] = testAction.invokeBlock[AnyContent](testRequest, testBlock)

    status(result) mustBe OK
    contentAsString(result) mustBe testResultBody
  }

}
