package forms.sa

import forms.behaviours.FormBehaviours
import models._
import models.sa._

class YourSaIsNotInThisAccountFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> YourSaIsNotInThisAccount.options.head.value
  )

  val form = new YourSaIsNotInThisAccountFormProvider()()

  "YourSaIsNotInThisAccount form" must {

    behave like questionForm[YourSaIsNotInThisAccount](YourSaIsNotInThisAccount.values.head)

    behave like formWithOptionField(
      Field(
        "value",
        Required -> "yourSaIsNotInThisAccount.error.required",
        Invalid -> "error.invalid"),
      YourSaIsNotInThisAccount.options.toSeq.map(_.value): _*)
  }
}
