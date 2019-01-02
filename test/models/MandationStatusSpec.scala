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

package models

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class MandationStatusSpec extends UnitSpec {

  val testJson = """{"mandationStatus":"MTDfB Mandated"}"""
  val mandationStatus = "MTDfB Mandated"

  "call unapply successfully to create as Json" in {
    val tokenModel = MandationStatus(mandationStatus)
    val json = Json.toJson(tokenModel)
    json.toString() shouldBe testJson
  }

  "call apply successfully to create model from Json" in {
    val request = Json.parse(testJson.toString).as[MandationStatus]
    request.mandationStatus shouldBe mandationStatus
  }

  "throw an exception if an unexpected mandation status is passed as a parameter" should {
    "throw an Exception" in {
      intercept[Exception] {
        MandationStatus("invalidStatus")
      }
    }
  }
}
