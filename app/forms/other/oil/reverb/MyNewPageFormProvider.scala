package forms.other.oil.reverb

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.other.oil.reverb.MyNewPage

class MyNewPageFormProvider @Inject() extends FormErrorHelper with Mappings {

  def apply(): Form[MyNewPage] =
    Form(
      "value" -> enumerable[MyNewPage]("myNewPage.error.required")
    )
}
