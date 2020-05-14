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

package services

import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import connectors.VatSubscriptionConnector
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class VatSubscriptionServiceSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
  val service: VatSubscriptionService = VatSubscriptionService(mockVatSubscriptionConnector)
  val testVrn: String = "vrn"

  "The VatSubscriptionService mandationStatus method" should {

    "return Some(true) when connector response is Right(true)" in {

      when(mockVatSubscriptionConnector.getMandationStatus(testVrn)).thenReturn(Future.successful(Right(true)))

      whenReady(service.getMandationStatus(testVrn)) {
        _ shouldBe Some(true)
      }
    }

    "return Some(false) when connector response is Right(false)" in {

      when(mockVatSubscriptionConnector.getMandationStatus(testVrn)).thenReturn(Future.successful(Right(false)))

      whenReady(service.getMandationStatus(testVrn)) {
        _ shouldBe Some(false)
      }
    }

    "return None when the connector returns a Left('Failed')" in {

      when(mockVatSubscriptionConnector.getMandationStatus(testVrn)).thenReturn(Future.successful(Left("Failed")))

      whenReady(service.getMandationStatus(testVrn)) {
        _ shouldBe None
      }
    }
  }
}
