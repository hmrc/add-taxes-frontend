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

package controllers.vat.moss.uk

import controllers._
import controllers.actions._
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.{Enrolments, UrlHelper}
import views.html.vat.moss.uk.addVATFirst
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach

class AddVATFirstControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  val mockUrlHelper: UrlHelper = mock[UrlHelper]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new AddVATFirstController(frontendAppConfig, messagesApi, FakeAuthAction, FakeServiceInfoAction, mockUrlHelper)

  def viewAsString() = addVATFirst(frontendAppConfig, mockUrlHelper)(HtmlFormat.empty)(fakeRequest, messages).toString

  override def beforeEach(): Unit = {
    reset(mockUrlHelper)
    when(mockUrlHelper.emacEnrollmentsUrl(Enrolments.VAT)).thenReturn("")
  }

  "AddVATFirst Controller" must {
    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}
