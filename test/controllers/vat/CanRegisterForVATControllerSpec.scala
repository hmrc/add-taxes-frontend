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

package controllers.vat

import controllers._
import controllers.actions._
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.FeatureToggleSupport
import uk.gov.hmrc.http.NotFoundException
import views.html.vat.canRegisterForVAT

class CanRegisterForVATControllerSpec extends ControllerSpecBase with BeforeAndAfterEach with FeatureToggleSupport {

  val view: canRegisterForVAT = injector.instanceOf[canRegisterForVAT]

  def controller(): CanRegisterForVATController = {
    new CanRegisterForVATController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      featureDepandantAction = app.injector.instanceOf[FeatureDependantAction],
      view
    )
  }

  def viewAsString(): String =
    new canRegisterForVAT(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  "CanRegisterForVAT Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
