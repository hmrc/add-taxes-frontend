/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.vat

import controllers.ControllerSpecBase

class ReferenceCheckerSpec extends ControllerSpecBase {

  object DummyChecker extends ModulusReferenceChecker {
    override val remainderLookupTable: List[Char] = List('A', 'B', 'C')
    override val modulus: Int = 65
    override val checkCharIndex: Int = 0

    override val weights: List[Int] = List(1, 2, 3)
    override val letterToNumber = Map.empty[Char, Int]
    override val refRegex = ".*"
  }

  "UtrReferenceChecker behaviour" should {

    object DummyUtrReferenceChecker extends UtrReferenceChecker {
      override val remainderLookupTable: List[Char] = List('2', '1', '9', '8', '7', '6', '5', '4', '3', '2', '1')
      override val modulus = 11
      override val checkCharIndex = 0

      override val weights: List[Int] = List(6, 7, 8, 9, 10, 5, 4, 3, 2)
      override val letterToNumber = Map.empty[Char, Int]
      override val refRegex = """^\d{10}$"""
    }

    "use referenceToValidate (tail) when computing validation" in {
      val tail = "123456789"
      val firstChar = '2'
      val ref = s"$firstChar$tail"

      ref.matches(DummyUtrReferenceChecker.refRegex) mustBe true
      DummyUtrReferenceChecker.isValid(ref)
    }

    "reject invalid format early" in {
      DummyUtrReferenceChecker.isValid("ABCDEFGHIJ") mustBe false
      DummyUtrReferenceChecker.isValid("123") mustBe false
    }
  }

  "SAReferenceChecker behaviour " should {

    "strip leading K" in {
      SelfAssessmentReferenceChecker.prepareReference("K1234567890") mustBe "1234567890"
    }

    "strip trailing K" in {
      SelfAssessmentReferenceChecker.prepareReference("1234567890K") mustBe "1234567890"
    }

    "leave other references unchanged" in {
      SelfAssessmentReferenceChecker.prepareReference("A123456789") mustBe "A123456789"
    }
  }

  "EPayeReferenceChecker behaviour" should {
    "pass extraCheck1 when correct conditions met" in {
      val ref = "961PA0" + "12345" + "X1"
      val extra1 = EpayeReferenceChecker.extraChecks(1)
      extra1(ref) mustBe true
    }

    "fail extraCheck2 when starts with 961 and char12 != X but does not match strict regex" in {
      val ref = "961PA1234ABCD"
      val extra2 = EpayeReferenceChecker.extraChecks(2)
      extra2(ref) mustBe false
    }

    "pass extraCheck2 with a correct 961 format" in {
      val ref = "961PA12345678"
      val extra2 = EpayeReferenceChecker.extraChecks(2)
      extra2(ref) mustBe true
    }
  }

  "OtherTaxReferenceChecker behaviour" should {

    "reject invalid references" in {
      val bad = "BAD123"
      OtherTaxReferenceChecker.isValid(bad) mustBe false
    }

    "match correct regex" in {
      val valid1 = "XA012345678901"
      valid1.matches(OtherTaxReferenceChecker.refRegex) mustBe true
    }
  }

  "SdltReferenceChecker" should {

    "reject invalid" in {
      SdltReferenceChecker.isValid("ABC") mustBe false
      SdltReferenceChecker.isValid("123456789ZZ") mustBe false
    }

    "validate format" in {
      val valid = "123456789MA"
      valid.matches(SdltReferenceChecker.refRegex) mustBe true
    }
  }
}