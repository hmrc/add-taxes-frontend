/*
 * Copyright 2018 HM Revenue & Customs
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

import connectors.FakeDataCacheConnector
import controllers.actions.{FakeServiceInfoAction, _}
import forms.OtherTaxesFormProvider
import models.OtherTaxes
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import play.api.data.Form
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.{FakeNavigator, RadioOption}
import views.html.{organisation_only, otherTaxes}

class OtherTaxesControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.IndexController.onPageLoad()

  def requestWithEnrolments(keys: String*): ServiceInfoRequest[AnyContent] = {
    val enrolments = Enrolments(keys.map(Enrolment(_)).toSet)
    ServiceInfoRequest[AnyContent](AuthenticatedRequest(FakeRequest(), "", enrolments, Some(Organisation)), HtmlFormat.empty)
  }

  val formProvider = new OtherTaxesFormProvider()
  val form = formProvider()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap, fakeAuthAction: AuthAction = FakeAuthAction) =
    new OtherTaxesController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), fakeAuthAction,
      FakeServiceInfoAction, formProvider)

  def viewAsString(form: Form[_] = form) = otherTaxes(frontendAppConfig, form, removeRadioOptionFromList())(HtmlFormat.empty)(fakeRequest, messages).toString
  def viewAsStringOrganisationOnly(request: ServiceInfoRequest[AnyContent]) = organisation_only(frontendAppConfig)(HtmlFormat.empty)(request, messages).toString()

  def removeRadioOptionFromList(radioOptionToRemove: Option[RadioOption] = None): Seq[RadioOption] = {
    val listOfAllRadioOptions: Seq[RadioOption] = Seq(
      RadioOption("otherTaxes", "alcoholAndTobaccoWholesalingAndWarehousing"),
      RadioOption("otherTaxes", "automaticExchangeOfInformation"),
      RadioOption("otherTaxes", "charities"),
      RadioOption("otherTaxes", "gamblingAndGaming"),
      RadioOption("otherTaxes", "oilAndFuel"),
      RadioOption("otherTaxes", "fulfilmentHouseDueDiligenceSchemeIntegration"),
      RadioOption("otherTaxes", "housingAndLand"),
      RadioOption("otherTaxes", "importsExports")
    )
    radioOptionToRemove.fold(listOfAllRadioOptions)(radioOptionToRemove =>
      listOfAllRadioOptions.filterNot(currentRadioOption => currentRadioOption.equals(radioOptionToRemove))
    )
  }

  "OtherTaxes Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "When a user is an individual, render the you can't add this business account view" in {
      val request = ServiceInfoRequest[AnyContent](AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual)), HtmlFormat.empty)
      val result = controller(fakeAuthAction = FakeAuthActionIndividual).onPageLoad()(request)

      status(result) mustBe OK
      val view = contentAsString(result)
      view mustBe viewAsStringOrganisationOnly(request)
    }

    "When a user is an agent, render the you can't add this business account view" in {
      val request = ServiceInfoRequest[AnyContent](AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Agent)), HtmlFormat.empty)
      val result = controller(fakeAuthAction = FakeAuthActionAgent).onPageLoad()(request)

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

    "return OK if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
    }

    "redirect to next page when valid data is submitted and no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", OtherTaxes.options.head.value))
      val result = controller(dontGetAnyData).onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "display all options" in {
      val request = requestWithEnrolments()
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList()
    }

    "not display alcohol if the user has HMCE-ATWD-ORG, HMRC-AWRS-ORG " in {
      val request = requestWithEnrolments("HMCE-ATWD-ORG", "HMRC-AWRS-ORG")
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "alcoholAndTobaccoWholesalingAndWarehousing")))
    }

    "not display AEOI if the user has HMRC-FATCA-ORG" in {
      val request = requestWithEnrolments("HMRC-FATCA-ORG")
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "automaticExchangeOfInformation")))
    }

    "not display Charities if the user has HMRC-CHAR-ORG" in {
      val request = requestWithEnrolments("HMRC-CHAR-ORG")
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "charities")))
    }
    "not display Gambling and Gaming if the user has HMRC-MGD-ORG, HMRC-GTS-GBD, HMRC-GTS-PBD, HMRC-GTS-RGD" in {
      val request = requestWithEnrolments("HMRC-MGD-ORG", "HMRC-GTS-GBD", "HMRC-GTS-PBD", "HMRC-GTS-RGD")
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "gamblingAndGaming")))
    }
    "not display Oil and Fuel if the user has HMCE-RO, HMCE-TO" in {
      val request = requestWithEnrolments("HMCE-RO", "HMCE-TO")
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "oilAndFuel")))
    }
    "not display Fulfilment House if the user has ETMPREGISTRATIONNUMBER" in {
      val request = requestWithEnrolments("ETMPREGISTRATIONNUMBER")
      val result = controller(dontGetAnyData).getOptions(request)

      result mustBe removeRadioOptionFromList(Some(RadioOption("otherTaxes", "fulfilmentHouseDueDiligenceSchemeIntegration")))
    }
  }
}
