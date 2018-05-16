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

package views.employer.cis.uk.contractor

import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.twirl.api.HtmlFormat
import utils.{Enrolments, UrlHelper}
import views.behaviours.ViewBehaviours
import views.html.employer.cis.uk.contractor.registerForPAYE

class RegisterForPAYEViewSpec extends ViewBehaviours with MockitoSugar with BeforeAndAfterEach{

  val messageKeyPrefix = "registerForPAYE"

  val mockUrlHelper: UrlHelper = mock[UrlHelper]

  override def beforeEach(): Unit = {
    reset(mockUrlHelper)
    when(mockUrlHelper.emacEnrollmentsUrl(Enrolments.AddCis)).thenReturn("")
  }

  def createView = () => registerForPAYE(frontendAppConfig, mockUrlHelper)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterForPAYE view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {

      when(mockUrlHelper.getPortalURL("businessRegistration")((fakeRequest)))
        .thenReturn("http://localhost:8080/portal/business-registration/introduction?lang=eng")

      val doc =  asDocument(createView())
      val view = doc.text()

      view must include("Register for PAYE for employers first")

      view must include("You need to be registered for PAYE for employers if you want to register for CIS as a contractor.")

      view must include("Register for PAYE for employers")

      view must include("I don’t want to do this right now")

      assertLinkById(
        doc,
        "continue",
        "Register for PAYE for employers",
        "http://localhost:8080/portal/business-registration/introduction?lang=eng",
        "CisUkContractorRegisterEpaye:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I don’t want to do this right now",
        "http://localhost:9020/business-account",
        "CisUkContractorRegisterEpaye:Click:NotNow"
      )
    }
  }
}
