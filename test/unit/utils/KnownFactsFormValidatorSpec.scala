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

package utils

import base.SpecBase
import play.api.data.FormError

class KnownFactsFormValidatorSpec extends SpecBase {

  val mockKnownFactsFormValidator: KnownFactsFormValidator = new KnownFactsFormValidator()

  "validate postcode" must {
    val blankPostcodeError = "TBC - Blank"
    val invalidPostcodeError = "TBC - Invalid"
    val postcodeKey = "postcode"

    "return Form Error when postcode is blank " in {
      val postcodeBlankData = Map("postcode" -> "")
      val result = mockKnownFactsFormValidator.validatePostcode(postcodeKey, blankPostcodeError,invalidPostcodeError)
      result.bind("postcode", postcodeBlankData).left.get.contains(FormError(postcodeKey, blankPostcodeError )) mustBe true
    }

    "return Form Error when postcode is invalid" in {
      val postcodeInvalidPostcode = Map("postcode" -> "!!!!!")
      val result = mockKnownFactsFormValidator.validatePostcode(postcodeKey, blankPostcodeError,invalidPostcodeError)
      result.bind("postcode", postcodeInvalidPostcode).left.get.contains(FormError(postcodeKey, invalidPostcodeError)) mustBe true
    }

    "return Form Error when postcode not the correct length" in {
      val postcodeInvalidPostcodeLength = Map("postcode" -> "AA1 1AAAAA")
      val result = mockKnownFactsFormValidator.validatePostcode(postcodeKey, blankPostcodeError,invalidPostcodeError)
      result.bind("postcode", postcodeInvalidPostcodeLength).left.get.contains(FormError(postcodeKey, invalidPostcodeError)) mustBe true
    }

    "return valid postcode" in {
      val validPostcode = Map("postcode" -> "AA1 1AA")
      val result = mockKnownFactsFormValidator.validatePostcode(postcodeKey, blankPostcodeError,invalidPostcodeError)
      result.bind("postcode", validPostcode).right.get.contains("AA1 1AA") mustBe true
    }
  }

  "validate nino" must {
    val ninoKey: String = "nino"
    val blankMessageKey: String = "TBC - Blank"
    val lengthMessageKey: String = "TBC - invalid Length"
    val formatMessageKey: String = "TBC - invalid format"

    "return Form Error when nino is blank " in {
      val ninoBlankData = Map("nino" -> "")
      val result = mockKnownFactsFormValidator.ninoFormatter(ninoKey, blankMessageKey, lengthMessageKey, formatMessageKey)
      result.bind("nino", ninoBlankData).left.get.contains(FormError(ninoKey, blankMessageKey)) mustBe true
     }

    "return Form Error when nino is invalid" in {
      val ninoInvalidData = Map("nino" -> "AAAAVVBGF")
      val result = mockKnownFactsFormValidator.ninoFormatter(ninoKey, blankMessageKey, lengthMessageKey, formatMessageKey)
      result.bind("nino", ninoInvalidData).left.get.contains(FormError(ninoKey, formatMessageKey)) mustBe true
    }

    "return Form Error when nino has invalid length" in {
      val ninoInvalidLength = Map("nino" -> "1234567891012")
      val result = mockKnownFactsFormValidator.ninoFormatter(ninoKey, blankMessageKey, lengthMessageKey, formatMessageKey)
      result.bind("nino", ninoInvalidLength).left.get.contains(FormError(ninoKey, lengthMessageKey)) mustBe true
    }

    "return valid nino" in {
      val validPostcode = Map("nino"-> "AA000000A")
      val result = mockKnownFactsFormValidator.ninoFormatter(ninoKey, blankMessageKey, lengthMessageKey, formatMessageKey)
      result.bind("nino", validPostcode).right.get.contains("AA000000A") mustBe true
    }
   }
}
