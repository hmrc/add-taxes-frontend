package forms.other.ppt

import forms.behaviours.FormBehaviours
import models.other.ppt.DoYouHaveAPptReference
import models.{Field, Invalid, Required}

class PptReferenceFormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> DoYouHaveAPptReference.options.head.value
  )

  val form = new PptReferenceFormProvider()()

  "DoYouNeedToStopVatMossNU form" must {

    behave like questionForm[DoYouHaveAPptReference](DoYouHaveAPptReference.values.head)

    behave like formWithOptionField(
      Field("value", Required -> "doYouHaveAPptRef.error.required", Invalid -> "error.invalid"),
      DoYouHaveAPptReference.options.toSeq.map(_.value): _*)
  }
}
