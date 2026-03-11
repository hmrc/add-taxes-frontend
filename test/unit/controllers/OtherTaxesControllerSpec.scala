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
import models.OtherTaxes.{GamblingAndGaming, OilAndFuel, PODS}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.scalatest.BeforeAndAfterEach
import play.api.data.Form
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual}
import uk.gov.hmrc.auth.core.Enrolments
import utils.{FakeNavigator, RadioOption}
import views.html.{organisation_only, otherTaxes}

class OtherTaxesControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  override def beforeEach(): Unit = {}

  def onwardRoute: Call = routes.IndexController.onPageLoad

  val formProvider           = new OtherTaxesFormProvider()
  val form: Form[OtherTaxes] = formProvider()

  private val otherTaxesView: otherTaxes              = injector.instanceOf[otherTaxes]
  private val organisationOnlyView: organisation_only = injector.instanceOf[organisation_only]

  def controller(fakeAuthAction: AuthAction = FakeAuthAction): OtherTaxesController =
    new OtherTaxesController(
      mcc,
      new FakeNavigator[Call](desiredRoute = onwardRoute),
      fakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      otherTaxesView,
      organisationOnlyView,
      frontendAppConfig
    )

  private val allOtherTaxOptions: Seq[RadioOption] = Seq(
    RadioOption("otherTaxes", "alcoholAndTobacco"),
    RadioOption("otherTaxes", "automaticExchangeOfInformation"),
    RadioOption("otherTaxes", "charities"),
    RadioOption("otherTaxes", "childTrustFund"),
    RadioOption("otherTaxes", "economicCrimeLevy"),
    RadioOption("otherTaxes", "fulfilmentHouseDueDiligenceSchemeIntegration"),
    RadioOption("otherTaxes", "gamblingAndGaming"),
    RadioOption("otherTaxes", "housingAndLand"),
    RadioOption("otherTaxes", "importsExports"),
    RadioOption("otherTaxes", "oilAndFuel"),
    RadioOption("otherTaxes", "pillar2"),
    RadioOption("otherTaxes", "pods"),
    RadioOption("otherTaxes", "ppt"),
    RadioOption("otherTaxes", "vapingDuty")
  )
  private val allEnrolmentKeys: Seq[String] = utils.Enrolments.values.map(_.identifier).toSeq

  def viewAsString(form: Form[_] = form): String =
    new otherTaxes(formWithCSRF, mainTemplate)(frontendAppConfig, form, allOtherTaxOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsStringOrganisationOnly(request: ServiceInfoRequest[AnyContent]): String =
    new organisation_only(formWithCSRF, mainTemplate)(frontendAppConfig)(HtmlFormat.empty)(request, messages).toString()

  def removeOptionsFromListOfAllRadioOptions(radioOptionsToRemove: Seq[RadioOption]): Seq[RadioOption] = allOtherTaxOptions.diff(radioOptionsToRemove)

  "getOptions" must {
    "show only the default radio options (AlcoholAndTobacco, HousingAndLand, ImportsExports, PPT)" when {
      "request contains all possible other tax enrolment keys" in {
        val request = requestWithEnrolments(keys = allEnrolmentKeys: _*)
        val result  = controller().getOptions(request)
        val defaultRadioOptions = Seq(
          RadioOption("otherTaxes", "alcoholAndTobacco"),
          RadioOption("otherTaxes", "housingAndLand"),
          RadioOption("otherTaxes", "importsExports"),
          RadioOption("otherTaxes", "ppt")
        )

        result mustBe defaultRadioOptions
      }
    }

    "show all possible OtherTax radio options" when {
      "request contains no enrolment keys" in {
        val request = requestWithEnrolments(keys = "")
        val result  = controller().getOptions(request).toList

        result mustBe allOtherTaxOptions
      }
    }

    val singleEnrolmentChecks = Seq(
      ("AutomaticExchangeOfInformation", "HMRC-FATCA-ORG", "automaticExchangeOfInformation"),
      ("Charities", "HMRC-CHAR-ORG", "charities"),
      ("ChildTrustFund", "IR-CTF", "childTrustFund"),
      ("ECL", "HMRC-ECL-ORG", "economicCrimeLevy"),
      ("FulfilmentHouseDueDiligenceSchemeIntegration", "EtmpRegistrationNumber", "fulfilmentHouseDueDiligenceSchemeIntegration"),
      ("Pillar2", "HMRC-PILLAR2-ORG", "pillar2"),
      ("VapingProductsDuty", "HMRC-VPD-ORG", "vapingDuty")
    )

    singleEnrolmentChecks.foreach { case (name, enrolmentKey, radioOptionName) =>
      s"show all but $name radio options" when {
        s"request contains $enrolmentKey enrolment key" in {
          val request = requestWithEnrolments(keys = enrolmentKey)
          val result  = controller().getOptions(request).toList

          result mustBe allOtherTaxOptions.filterNot(_ == RadioOption("otherTaxes", radioOptionName))
        }
      }
    }

    val generalBetting = "HMRC-GTS-GBD"
    val machineGaming  = "HMRC-MGD-ORG"
    val poolBetting    = "HMRC-GTS-PBD"
    val remoteGaming   = "HMRC-GTS-RGD"
    "show the GamblingAndGaming radio option" when {
      "request does not contain all four enrolment keys" when {
        "all but 'GeneralBetting'" in {
          val request = requestWithEnrolments(keys = machineGaming, poolBetting, remoteGaming)
          val result  = controller().getOptions(request)

          result.contains(GamblingAndGaming.toRadioOption) mustBe true
        }
        "all but 'MachineGamingDuty'" in {
          val request = requestWithEnrolments(keys = generalBetting, poolBetting, remoteGaming)
          val result  = controller().getOptions(request)

          result.contains(GamblingAndGaming.toRadioOption) mustBe true
        }
        "all but 'PoolBetting'" in {
          val request = requestWithEnrolments(keys = generalBetting, machineGaming, remoteGaming)
          val result  = controller().getOptions(request)

          result.contains(GamblingAndGaming.toRadioOption) mustBe true
        }
        "all but 'RemoteGaming'" in {
          val request = requestWithEnrolments(keys = generalBetting, machineGaming, poolBetting)
          val result  = controller().getOptions(request)

          result.contains(GamblingAndGaming.toRadioOption) mustBe true
        }
      }
    }
    "do NOT show the GamblingAndGaming radio option" when {
      "request contains all four enrolment keys" in {
        val request = requestWithEnrolments(keys = generalBetting, machineGaming, poolBetting, remoteGaming)
        val result  = controller().getOptions(request)

        result.contains(GamblingAndGaming.toRadioOption) mustBe false
      }
    }

    val rebatedOils = "HMCE-RO"
    val tiedOils    = "HMCE-TO"
    "show the OilAndFuel radio option" when {
      "request does not contain 'RebatedOils' enrolment key" in {
        val request = requestWithEnrolments(keys = rebatedOils)
        val result  = controller().getOptions(request)

        result.contains(OilAndFuel.toRadioOption) mustBe true
      }
      "request does not contain 'TiedOils' enrolment key" in {
        val request = requestWithEnrolments(keys = tiedOils)
        val result  = controller().getOptions(request)

        result.contains(OilAndFuel.toRadioOption) mustBe true
      }
    }
    "do NOT show the OilAndFuel radio option" when {
      "request contains both enrolment keys" in {
        val request = requestWithEnrolments(keys = rebatedOils, tiedOils)
        val result  = controller().getOptions(request)

        result.contains(OilAndFuel.toRadioOption) mustBe false
      }
    }

    val podsorg = "HMRC-PODS-ORG"
    val podspp  = "HMRC-PODSPP-ORG"
    "show the PODS radio option" when {
      "request does not contain 'PODSORG' enrolment key" in {
        val request = requestWithEnrolments(keys = podsorg)
        val result  = controller().getOptions(request)

        result.contains(OilAndFuel.toRadioOption) mustBe true
      }
      "request does not contain 'PODSPP' enrolment key" in {
        val request = requestWithEnrolments(keys = podspp)
        val result  = controller().getOptions(request)

        result.contains(PODS.toRadioOption) mustBe true
      }
    }
    "do NOT show the PODS radio option" when {
      "request contains both enrolment keys" in {
        val request = requestWithEnrolments(keys = podsorg, podspp)
        val result  = controller().getOptions(request)

        result.contains(PODS.toRadioOption) mustBe false
      }
    }
  }

  "onPageLoad" must {
    "render the 'You can't add this business account' view" when {
      "the user is an individual" in {
        val request = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest().withMethod("GET"), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
          HtmlFormat.empty)

        val result = controller(new FakeAuthActionIndividual(parser)).onPageLoad()(request)

        status(result) mustBe OK
        val view = contentAsString(result)
        view mustBe viewAsStringOrganisationOnly(request)
      }

      "the user is an agent" in {
        val request = ServiceInfoRequest[AnyContent](
          AuthenticatedRequest(FakeRequest().withMethod("GET"), "", Enrolments(Set()), Some(Agent), groupId, providerId, confidenceLevel, None),
          HtmlFormat.empty)

        val result = controller(fakeAuthAction = new FakeAuthActionAgent(parser)).onPageLoad()(request)

        status(result) mustBe OK
        val view = contentAsString(result)
        view mustBe viewAsStringOrganisationOnly(request)
      }
    }

    "render the 'Select a category' other tax view" when {
      "the user is an 'Organisation'" in {
        val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString()
      }
    }
  }

  "onSubmit" must {
    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", OtherTaxes.options.head.value)).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value")).withMethod("POST")
      val boundForm   = form.bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }
  }

}
