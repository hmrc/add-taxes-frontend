package forms.employer.cis

import forms.behaviours.FormBehaviours
import models._
import models.employer.cis._

class IsYourBusinessInUKFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> IsYourBusinessInUK.options.head.value
  )

  val form = new IsYourBusinessInUKFormProvider()()

  "IsYourBusinessInUK form" must {

    behave like questionForm[IsYourBusinessInUK](IsYourBusinessInUK.values.head)

    behave like formWithOptionField(
      Field(
        "value",
        Required -> "isYourBusinessInUK.error.required",
        Invalid -> "error.invalid"),
      IsYourBusinessInUK.options.toSeq.map(_.value): _*)
  }
}
