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

package service

import connectors.{DataCacheConnector, SaConnector}
import controllers.ControllerSpecBase
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.IvLinks
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier
import controllers.sa.{routes => saRoutes}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class SaServiceSpec extends ControllerSpecBase with MockitoSugar {

  val mockSaConnector: SaConnector = mock[SaConnector]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val testIvLinks: IvLinks = IvLinks("/iv-link", "/journeyLink")

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel),
    HtmlFormat.empty)

  def service() = new SaService(
    mockSaConnector,
    mockDataCacheConnector,
    frontendAppConfig
  )


  "Sa service" when {
    "getIvRedirectLink is called" must {
      "return redirect url of Try Pin in post when fails to retrieve iv links" in {
        when(mockSaConnector.getIvLinks(any())(any(), any())).thenReturn(Future.successful(None))

        val result = service().getIvRedirectLink("1234567890")

        whenReady(result) { result =>
          result mustBe saRoutes.TryPinInPostController.onPageLoad().url
        }
      }

      "return redirect url from retrieve iv links" in {
        when(mockSaConnector.getIvLinks(any())(any(), any())).thenReturn(Future.successful(Some(testIvLinks)))

        val result = service().getIvRedirectLink("1234567890")

        whenReady(result) { result =>
          result must include("/iv-link")
        }
      }
    }
  }
}
