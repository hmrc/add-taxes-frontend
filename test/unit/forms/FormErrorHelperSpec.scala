package forms

import base.SpecBase
import play.api.data.FormError

class FormErrorHelperSpec extends SpecBase {

  object TestHelper extends FormErrorHelper
  
  "return a Left containing a sequence with one FormError" in {
    val result = TestHelper.produceError("username", "required")

    result mustBe Left(Seq(FormError("username", "required")))
  }

  "create the correct FormError object" in {
    val Left(errors) = TestHelper.produceError("password", "tooShort")

    errors.size mustBe 1
    errors.head.key mustBe "password"
    errors.head.message mustBe "tooShort"
  }
}
