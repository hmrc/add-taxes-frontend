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

package controllers.sa

import controllers.Assets.{Redirect, SEE_OTHER}
import controllers.ControllerSpecBase
import controllers.sa.{routes => saRoutes}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import playconfig.featuretoggle.FeatureToggleSupport
import service.IvService

import scala.concurrent.Future

class IvJourneyControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  val mockIvService: IvService = mock[IvService]
  val btaOrigin: String = "bta-sa"

  def controller(): IvJourneyController = {
    new IvJourneyController(
      mockIvService,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      frontendAppConfig
    )
  }

  "IvJourney Controller" must {
    "redirect to enrolment success page when checkAndEnrol returns redirect to enrolment success" in {
      when(mockIvService.ivCheckAndEnrol(any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin))))

      val result = controller().ivRouter(btaOrigin)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).get mustBe saRoutes.EnrolmentSuccessController.onPageLoad(btaOrigin).url
    }

    "redirect to try pin and post page when checkAndEnrol returns redirect to try pin and post page" in {
      when(mockIvService.ivCheckAndEnrol(any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin))))

      val result = controller().ivRouter(btaOrigin)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).get mustBe saRoutes.RetryKnownFactsController.onPageLoad(btaOrigin).url
    }
  }

}
