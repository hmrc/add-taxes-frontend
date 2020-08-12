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

import scala.util.Try

trait ReferenceChecker {

  val letterToNumber: Map[Char, Int]
  val weights: List[Int]

  val refRegex: String
  def extraChecks: List[String=>Boolean] = List(ref => ref.matches(refRegex))
  def mainCheck(reference:String, weightedSum:Int): Boolean

  def referenceToValidate(reference:String): String = reference
  def prepareReference(reference:String): String = reference

  implicit class StringImprovements(s: String) {
    def dropCharAtIndex(n: Int) = {
      val (l1, l2) = s.toList splitAt n
      val charList = l1 ::: (l2 drop 1)
      charList.mkString
    }
  }

  implicit class CharImprovements(digitOrLetter: Char) {
    def toNumber: Int = letterToNumber.getOrElse(digitOrLetter, digitOrLetter.asDigit)
  }

  def isValid(reference: String): Boolean = {
    val preparedReference = prepareReference(reference.toUpperCase)

    val mainCheckResult = Try {

      val refToValidate = referenceToValidate(preparedReference)

      val weightedSum = weights.zipWithIndex.collect {
        case (weight, index) if index < refToValidate.length => {
          weight * refToValidate(index).toNumber
        }
      }.sum

      mainCheck(preparedReference, weightedSum)

    } getOrElse false

    extraChecks.foldLeft(mainCheckResult) { (fullCondition, extraCheck) =>
      fullCondition && extraCheck(preparedReference.toUpperCase)
    }

  }
}


trait DifferenceReferenceChecker extends ReferenceChecker {

  def mainCheck(reference:String, weightedSum:Int) = {
    def checkVatReference(sum: Int, expected: Int): Boolean = {
      var sumLessThan97 = sum
      while (sumLessThan97 > 97) {
        sumLessThan97 -= 97
      }
      val calculatedDigits = 97 - sumLessThan97
      calculatedDigits == expected
    }

    val expected = reference.takeRight(2).toInt
    checkVatReference(weightedSum, expected) || checkVatReference(weightedSum + 55, expected)
  }
}

object VatReferenceChecker extends DifferenceReferenceChecker {
  val weights = List(8, 7, 6, 5, 4, 3, 2)
  val letterToNumber = Map.empty[Char,Int]
  val refRegex = """^\d{9}$"""
}


