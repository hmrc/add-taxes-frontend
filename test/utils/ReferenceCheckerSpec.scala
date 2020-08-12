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

import org.scalacheck.Gen
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class ModulusCheckerSpec extends WordSpecLike with Matchers with ScalaCheckDrivenPropertyChecks with TableDrivenPropertyChecks {

  "The VAT reference checker" should {

    val ValidReferences = Seq("101747696", "101747641", "000000097")
    val InvalidReferences = Seq("", "123456", "123456789", "123abc789")

    "return true for a valid reference" in {
      checkAllValid(VatReferenceChecker, ValidReferences)
    }

    "return false for an invalid reference" in {
      checkAllInvalid(VatReferenceChecker, InvalidReferences)
    }

    val vatWeights = List(8, 7, 6, 5, 4, 3, 2)

    "return true for correctly generated references for modulo 97" in {
      val checkSum = Gen.listOfN(7, Gen.chooseNum(0, 9))

      forAll(checkSum) { (ints: Seq[Int]) =>
        whenever(ints.size == 7) {
          var sums = (ints, vatWeights).zipped.map {
            case (s, i) => s * i
          }.sum

          while (sums > 97) sums -= 97

          val reference = ints.mkString + "%02d".format(97 - sums)
          VatReferenceChecker.isValid(reference) shouldBe true
        }
      }
    }

    "return true for correctly generated references for modulo 97 55" in {
      val checkSum = Gen.listOfN(7, Gen.chooseNum(0, 9))

      forAll(checkSum) { (ints: Seq[Int]) =>
        whenever(ints.size == 7) {
          var sums = (ints, vatWeights).zipped.map {
            case (s, i) => s * i
          }.sum + 55

          while (sums > 97) sums -= 97

          val reference = ints.mkString + "%02d".format(97 - sums)
          VatReferenceChecker.isValid(reference) shouldBe true
        }
      }
    }
  }



  def checkAllValid(checker: ReferenceChecker, refs: Seq[String]) = {
    val invalidRefs = refs.filterNot(checker.isValid)

    withClue("Invalid references found: " + invalidRefs + "\n") {
      invalidRefs should be (empty)
    }
  }

  def checkAllInvalid(checker: ReferenceChecker, refs: Seq[String]) = {
    val validRefs = refs.filter(checker.isValid)

    withClue("Valid references found: " + validRefs + "\n") {
      validRefs should be (empty)
    }
  }
}
