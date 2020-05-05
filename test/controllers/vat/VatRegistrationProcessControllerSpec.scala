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
import playconfig.featuretoggle.{FeatureToggleSupport, NewVatJourney}
import uk.gov.hmrc.http.NotFoundException
import views.html.vat.vatRegistrationProcess

class VatRegistrationProcessControllerSpec extends ControllerSpecBase with FeatureToggleSupport with BeforeAndAfterEach {

  val view: vatRegistrationProcess = injector.instanceOf[vatRegistrationProcess]

  def controller(): VatRegistrationProcessController = {
    new VatRegistrationProcessController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      featureDepandantAction = app.injector.instanceOf[FeatureDependantAction],
      view
    )
  }

  def viewAsString(): String =
    new vatRegistrationProcess(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  override def beforeEach(): Unit = {
    super.beforeEach()
    enable(NewVatJourney)
  }

  "VATRegistrationProcess Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "return exception when newVatJourney is disabled" in {
      disable(NewVatJourney)
      intercept[NotFoundException] {
        val result = controller().onPageLoad()(fakeRequest)
        await(result)
      }
    }
  }
}
