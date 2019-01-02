/*
 * Copyright 2019 HM Revenue & Customs
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

package services

import models.MandationStatus
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import connectors.VatSubscriptionConnector
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatSubscriptionServiceSpec extends UnitSpec with MockitoSugar with ScalaFutures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
  val service: VatSubscriptionService = VatSubscriptionService(mockVatSubscriptionConnector)
  val testVrn: String = "vrn"

  "The VatSubscriptionService mandationStatus method" when {

    "the connector returns a mandation status" should {

      "return Some(true) when status is MTDfB Mandated" in {

        when(mockVatSubscriptionConnector.getMandationStatus(testVrn))
          .thenReturn(Future.successful(Right(Some(MandationStatus("MTDfB Mandated")))))

        whenReady(service.getMandationStatus(testVrn)) {
          _ shouldBe Some(true)
        }
      }

      "return Some(true) when status is MTDfB Voluntary" in {

        when(mockVatSubscriptionConnector.getMandationStatus(testVrn))
          .thenReturn(Future.successful(Right(Some(MandationStatus("MTDfB Voluntary")))))

        whenReady(service.getMandationStatus(testVrn)) {
          _ shouldBe Some(true)
        }
      }

      "return Some(true) when status is Non MTDfB" in {

        when(mockVatSubscriptionConnector.getMandationStatus(testVrn))
          .thenReturn(Future.successful(Right(Some(MandationStatus("Non MTDfB")))))

        whenReady(service.getMandationStatus(testVrn)) {
          _ shouldBe Some(false)
        }
      }

      "return Some(true) when status is Non Digital" in {

        when(mockVatSubscriptionConnector.getMandationStatus(testVrn))
          .thenReturn(Future.successful(Right(Some(MandationStatus("Non Digital")))))

        whenReady(service.getMandationStatus(testVrn)) {
          _ shouldBe Some(false)
        }
      }
    }

    "the connector returns a Left('Failed')" should {

      "return None" in {

        when(mockVatSubscriptionConnector.getMandationStatus(testVrn))
          .thenReturn(Future.successful(Left("Failed")))

        whenReady(service.getMandationStatus(testVrn)) {
          _ shouldBe None
        }
      }
    }
  }
}
