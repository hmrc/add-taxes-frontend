package forms

import forms.behaviours.FormBehaviours
import models.other.oil.reverb._

class MyNewPageFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> MyNewPage.options.head.value
  )

  val form = new MyNewPageFormProvider()()

  "MyNewPage form" must {

    behave like questionForm[MyNewPage](MyNewPage.values.head)

    behave like formWithOptionField(
      Field(
        "value",
        Required -> "myNewPage.error.required",
        Invalid -> "error.invalid"),
      MyNewPage.options.toSeq.map(_.value): _*)
  }
}
