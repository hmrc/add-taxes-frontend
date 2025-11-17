
package config

import base.SpecBase
import play.api.mvc.PathBindable
import utils.Enrolments

class BindersSpec extends SpecBase {

  private val binder: PathBindable[Enrolments] = Binders.enrolmentBinder

  "enrolmentBinder.bind" should {

    "successfully bind a valid enrolment" in {
      val validEnrolment = Enrolments.values.head
      val input = s"   ${validEnrolment.toString.toLowerCase}   "

      binder.bind("key", input) mustBe Right(validEnrolment)
    }

    "return an error when enrolment is not found" in {
      binder.bind("key", "unknownValue") mustBe Left("Enrolment not found")
    }
  }

  "enrolmentBinder.unbind" should {

    "return the string representation of a enrolment" in {
      val enrolment = Enrolments.values.head
      binder.unbind("key", enrolment) mustBe enrolment.toString
    }
  }
}