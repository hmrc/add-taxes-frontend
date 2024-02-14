package utils

import controllers.vat.VatReferenceChecker
import org.scalacheck.Gen

trait TestOnlyVrnGenerator {

 case class Vrn(value: String)

  object Vrn {

    private val vatRegex = """^\d{9}$"""

    def isValid(input: String): Either[String, Vrn] = {
      if (input matches vatRegex) {
        if (VatReferenceChecker.isValid(input)) {
          Right(Vrn(input))
        } else {Left("VRN failed modulus check")}
      } else {Left("VRN failed regex check")}
    }
  }

  val randomString: Gen[String] = Gen.choose(9900000,9999999).map(i => VrnChecksum.apply(i.toString))
  val vrnGeneratorValid: Gen[String] = randomString.retryUntil(i => Vrn.isValid(i).isRight)
  val vrnGeneratorInValid: Gen[String] = randomString.retryUntil(i => Vrn.isValid(i).isLeft)

  def generateValidVrns(howMany: Int): Unit = {
    for(x <- 1 to howMany) {
      println(s"${vrnGeneratorValid.sample.get}")
    }
  }

  object VrnChecksum {

    def apply(s: String): String = s + calcCheckSum97(weightedTotal(s))

    def isValid(vrn: String): Boolean = {
      if (regexCheck(vrn)) {
        val total = weightedTotal(vrn)
        val checkSumPart = takeCheckSumPart(vrn)
        if (checkSumPart == calcCheckSum97(total).toInt) {true}
        else {checkSumPart == calcCheckSum9755(total)}
      } else {false}
    }

    private def calcCheckSum97(total: Int): String = {
      val x = total % 97 - 97
      f"${Math.abs(x)}%02d"
    }

    private def weightedTotal(reference: String): Int = {
      val weighting = List(8, 7, 6, 5, 4, 3, 2)
      val ref = reference.map(_.asDigit).take(7)
      (ref, weighting).zipped.map(_ * _).sum
    }

    private def calcCheckSum9755(total: Int): Int = calcCheckSum97(total + 55).toInt

    private def takeCheckSumPart(vrn: String): Int = vrn.takeRight(2).toInt

    private def regexCheck(vrn: String): Boolean = vrn.matches("[0-9]{9}")

  }
}
