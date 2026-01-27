/*
 * Copyright 2025 HM Revenue & Customs
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

import config.featureToggles.FeatureSwitch.VATKnownFactsCheck
import config.featureToggles.FeatureToggleSupport
import controllers.ControllerSpecBase
import forms.vat.WhatIsYourVATRegNumberFormProvider
import handlers.ErrorHandler
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.KnownFactsService
import uk.gov.hmrc.http.HeaderNames
import views.html.vat.{differentVatRegistrationNumbers, vatAccountUnavailable, vatRegistrationException, whatIsYourVATRegNumber}

import scala.concurrent.Future

class WhatIsYourVATRegNumberControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport with BeforeAndAfterEach {

  val testVrn                                       = "968501689"
  val formProvider                                  = new WhatIsYourVATRegNumberFormProvider()
  val form: Form[String]                            = formProvider()
  lazy val mockKnownFactsService: KnownFactsService = mock[KnownFactsService]

  val vatAccountUnavailablePage: vatAccountUnavailable                     = injector.instanceOf[vatAccountUnavailable]
  val whatIsYourVATRegNumberPage: whatIsYourVATRegNumber                   = injector.instanceOf[whatIsYourVATRegNumber]
  val differentVatRegistrationNumbersPage: differentVatRegistrationNumbers = injector.instanceOf[differentVatRegistrationNumbers]
  val vatRegistrationExceptionPage: vatRegistrationException               = injector.instanceOf[vatRegistrationException]
  val errorHandler: ErrorHandler                                           = injector.instanceOf[ErrorHandler]

  def controller(): WhatIsYourVATRegNumberController =
    new WhatIsYourVATRegNumberController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      vatAccountUnavailablePage,
      whatIsYourVATRegNumberPage,
      differentVatRegistrationNumbersPage,
      mockKnownFactsService
    )

  override def beforeEach(): Unit =
    reset(mockKnownFactsService)

  def whatIsYourVATRegNumberViewAsString(isKnownFactsCheckEnabled: Boolean, form: Form[_] = form): String =
    whatIsYourVATRegNumberPage(frontendAppConfig, form, isKnownFactsCheckEnabled)(HtmlFormat.empty)(fakeRequest, messages).toString

  def vatAccountUnavailableViewAsString(): String =
    vatAccountUnavailablePage(frontendAppConfig)(fakeRequest, messages).toString

  def differentVatRegistrationNumbersAsString(): String =
    differentVatRegistrationNumbersPage(frontendAppConfig)(fakeRequest, messages).toString

  "WhatIsYourVATRegNumberController" when {

    "onPageLoad" must {
      "return OK and the correct view for a GET" when {
        "'VATKnownFactsCheck' switch is enabled" in {
          enable(VATKnownFactsCheck)
          val result              = controller().onPageLoad()(fakeRequest.withMethod("GET"))
          val expectedViewContent = whatIsYourVATRegNumberViewAsString(isKnownFactsCheckEnabled = true)

          status(result) mustBe OK
          contentAsString(result) mustBe expectedViewContent
        }
        "'VATKnownFactsCheck' switch is disabled" in {
          disable(VATKnownFactsCheck)
          val result              = controller().onPageLoad()(fakeRequest.withMethod("GET"))
          val expectedViewContent = whatIsYourVATRegNumberViewAsString(isKnownFactsCheckEnabled = false)

          status(result) mustBe OK
          contentAsString(result) mustBe expectedViewContent
        }
      }
    }

    "onPageLoadVatUnavailable" must {
      "return OK and the correct view for a GET" in {
        val result              = controller().onPageLoadVatUnavailable()(fakeRequest.withMethod("GET"))
        val expectedViewContent = vatAccountUnavailableViewAsString()

        status(result) mustBe OK
        contentAsString(result) mustBe expectedViewContent
      }
    }

    "differentVatRegistrationNumbers" must {
      "return OK and the correct view for a GET" in {
        val result              = controller().onPageLoadDifferentVatRegistrationNumbers()(fakeRequest.withMethod("GET"))
        val expectedViewContent = differentVatRegistrationNumbersAsString()

        status(result) mustBe OK
        contentAsString(result) mustBe expectedViewContent
      }
    }

    "onSubmit" should {
      "return a BadRequest with errors when invalid data is submitted" in {
        enable(VATKnownFactsCheck)
        val postRequest    = fakeRequest.withFormUrlEncodedBody(("value", "")).withMethod("GET")
        val errorBoundForm = form.bind(Map("value" -> ""))

        val result              = controller().onSubmit()(postRequest)
        val expectedViewContent = whatIsYourVATRegNumberViewAsString(isKnownFactsCheckEnabled = true, errorBoundForm)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe expectedViewContent
      }

      "follow the error redirect returned from the service" when {
        "call to checkVrnMatchesPreviousAttempts fails and returns a redirect in a Left" in {
          when(mockKnownFactsService.checkVrnMatchesPreviousAttempts(any())(any(), any(), any()))
            .thenReturn(Future.successful(Left(Redirect("/redirect-call"))))

          val postRequest =
            fakeRequest.withFormUrlEncodedBody(("value", testVrn)).withMethod("POST").withHeaders(HeaderNames.xSessionId -> "sessionId")
          val result = controller().onSubmit()(postRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some("/redirect-call")
          verify(mockKnownFactsService, times(1)).checkVrnMatchesPreviousAttempts(any())(any(), any(), any())
        }
      }

      "redirect to the next page when valid data is submitted and mandation status is 200" in {
        when(mockKnownFactsService.checkVrnMatchesPreviousAttempts(any())(any(), any(), any()))
          .thenReturn(Future.successful(Right(testVrn)))

        val claimSubscriptionPage = frontendAppConfig.vatSignUpClaimSubscriptionUrl(testVrn)
        val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testVrn)).withMethod("POST").withHeaders(HeaderNames.xSessionId -> "sessionId")
        val result      = controller().onSubmit()(postRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(claimSubscriptionPage)
      }
    }
  }

}
