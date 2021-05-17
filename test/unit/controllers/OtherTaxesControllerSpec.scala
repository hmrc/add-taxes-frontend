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

import controllers.actions._
import forms.OtherTaxesFormProvider
import models.OtherTaxes
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import play.api.data.Form
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}
import utils.{FakeNavigator, RadioOption}
import views.html.{organisation_only, otherTaxes}

import scala.concurrent.ExecutionContext.Implicits.global

class OtherTaxesControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = routes.IndexController.onPageLoad()

  val formProvider = new OtherTaxesFormProvider()
  val form: Form[OtherTaxes] = formProvider()

  val view: otherTaxes = injector.instanceOf[otherTaxes]
  val orgOnly: organisation_only = injector.instanceOf[organisation_only]

  def controller(fakeAuthAction: AuthAction = FakeAuthAction): OtherTaxesController = {
    new OtherTaxesController(
      frontendAppConfig,
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      fakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      orgOnly
    )
  }

  val listOfAllRadioOptions: Seq[RadioOption] = Seq(
    RadioOption("otherTaxes", "alcoholAndTobaccoWholesalingAndWarehousing"),
    RadioOption("otherTaxes", "automaticExchangeOfInformation"),
    RadioOption("otherTaxes", "charities"),
    RadioOption("otherTaxes", "childTrustFund"),
    RadioOption("otherTaxes", "fulfilmentHouseDueDiligenceSchemeIntegration"),
    RadioOption("otherTaxes", "gamblingAndGaming"),
    RadioOption("otherTaxes", "housingAndLand"),
    RadioOption("otherTaxes", "importsExports"),
    RadioOption("otherTaxes", "oilAndFuel"),
    RadioOption("otherTaxes", "pods")
  )

  def viewAsString(form: Form[_] = form): String =
    new otherTaxes(formWithCSRF, mainTemplate)(frontendAppConfig, form, removeRadioOptionFromList())(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsStringOrganisationOnly(request: ServiceInfoRequest[AnyContent]): String =
    new organisation_only(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(request, messages).toString()

  def removeRadioOptionFromList(radioOptionToRemove: Option[RadioOption] = None): Seq[RadioOption] = {
    radioOptionToRemove.fold(listOfAllRadioOptions)(radioOptionToRemove =>
      listOfAllRadioOptions.filterNot(currentRadioOption => currentRadioOption.equals(radioOptionToRemove)))
  }

  "OtherTaxes Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "When a user is an individual, render the you can't add this business account view" in {
      val request = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
        HtmlFormat.empty)

      val result = controller(new FakeAuthActionIndividual(parser)).onPageLoad()(request)

      status(result) mustBe OK
      val view = contentAsString(result)
      view mustBe viewAsStringOrganisationOnly(request)
    }

    "When a user is an agent, render the you can't add this business account view" in {
      val request = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Agent), groupId, providerId, confidenceLevel),
        HtmlFormat.empty)

      val result = controller(fakeAuthAction = new FakeAuthActionAgent(parser)).onPageLoad()(request)

      status(result) mustBe OK
      val view = contentAsString(result)
      view mustBe viewAsStringOrganisationOnly(request)
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", OtherTaxes.options.head.value))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", OtherTaxes.options.head.value))
      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "display all options" in {
      val request = requestWithEnrolments(keys = "")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList()
    }

    "display alcohol if the user has HMCE-ATWD-ORG, HMRC-AWRS-ORG " in {
      val request = requestWithEnrolments("HMCE-ATWD-ORG", "HMRC-AWRS-ORG")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList()
    }

    "not display AEOI if the user has HMRC-FATCA-ORG" in {
      val request = requestWithEnrolments("HMRC-FATCA-ORG")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "automaticExchangeOfInformation")))
    }

    "not display Charities if the user has HMRC-CHAR-ORG" in {
      val request = requestWithEnrolments("HMRC-CHAR-ORG")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "charities")))
    }
    "not display Child Trust Fund if the user has IR-CTF" in {
      val request = requestWithEnrolments("IR-CTF")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "childTrustFund")))
    }
    "not display Gambling and Gaming if the user has HMRC-MGD-ORG, HMRC-GTS-GBD, HMRC-GTS-PBD, HMRC-GTS-RGD" in {
      val request = requestWithEnrolments("HMRC-MGD-ORG", "HMRC-GTS-GBD", "HMRC-GTS-PBD", "HMRC-GTS-RGD")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "gamblingAndGaming")))
    }
    "not display Oil and Fuel if the user has HMCE-RO, HMCE-TO" in {
      val request = requestWithEnrolments("HMCE-RO", "HMCE-TO")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "oilAndFuel")))
    }

    "not display Manage pensions if the user has both HMRC-PODS-ORG and HMRC-PODSPP-ORG" in {
      val request = requestWithEnrolments("HMRC-PODS-ORG", "HMRC-PODSPP-ORG")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "pods")))
    }

    "not display Manage pensions if the user has HMRC-PODS-ORG" in {
      val request = requestWithEnrolments("HMRC-PODS-ORG")
      val result = controller().getOptions(request)

      result mustBe listOfAllRadioOptions
    }

    "not display Manage pensions if the user has HMRC-PODSPP-ORG" in {
      val request = requestWithEnrolments("HMRC-PODSPP-ORG")
      val result = controller().getOptions(request)

      result mustBe listOfAllRadioOptions
    }

    "not display Fulfilment House if the user has HMRC-OBTDS-ORG and an EtmpRegistrationNumber" in {
      val enrolment =
        Enrolment("HMRC-OBTDS-ORG", Seq(EnrolmentIdentifier("EtmpRegistrationNumber", "123")), "Activated")
      val request = ServiceInfoRequest[AnyContent](
        AuthenticatedRequest(FakeRequest(), "", Enrolments(Set(enrolment)), Some(Organisation), groupId, providerId, confidenceLevel),
        HtmlFormat.empty)
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(
        Some(RadioOption("otherTaxes", "fulfilmentHouseDueDiligenceSchemeIntegration")))
    }

    "display Fulfilment House if the user has HMRC-OBTDS-ORG but no EtmpRegistrationNumber" in {
      val request = requestWithEnrolments("HMRC-OBTDS-ORG")
      val result = controller().getOptions(request)

      result mustBe removeRadioOptionFromList(None)
    }
  }
}
