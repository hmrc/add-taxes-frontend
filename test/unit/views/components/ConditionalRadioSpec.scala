package views.components

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages
import play.twirl.api.Html
import utils.RadioOption
import views.html.components.conditional_radio

class ConditionalRadioSpec extends SpecBase {

    val testLegend = ""

    val id = "testId"

    val testRadios: Seq[RadioOption] =
      Seq(RadioOption("haveYouRegisteredPartnership.Yes", "Yes"), RadioOption("haveYouRegisteredPartnership.No", "No"))

    val testForm = Form(
      "value" -> boolean
    )

  implicit val messagesImpl: Messages = messages

  val conditional_radio_injector: conditional_radio = injector.instanceOf[conditional_radio]

  def conditionalRadio(
                        form: Form[_],
                        hint: Option[Html] = Some(Html(messages("haveYouRegisteredPartnership.hintText"))),
                        dropDownContent: Option[Html] = Some(Html(messages("conditionalHint")))
                      ): Html = conditional_radio_injector(
      form = form,
      headingKey = "haveYouRegisteredPartnership.heading",
      id = "haveYouRegisteredPartnership",
      hint = hint,
      dropDownContent = dropDownContent
    )

    val testField: Field = testForm("value")

    "conditionalRadio" must {
      "not include error markups when form does not have errors" in {
        val doc: Document = Jsoup.parse(conditionalRadio(testForm).toString)

        val forms = doc.select("div.govuk-form-group")
        forms.size mustBe 1

        forms.get(0).className().split(" ").filter(_.nonEmpty) mustBe Array("govuk-form-group")
      }

      "include hint text as aria-describedby for fieldset when hint is present" in {
        val doc: Document = Jsoup.parse(conditionalRadio(testForm).toString)

        val forms = doc.select("fieldset")
        forms.size mustBe 1

        doc.getElementById("value-hint").text mustBe "We will have sent you a Unique Taxpayer Reference (UTR) for your partnership, if you have already registered it."
      }

      "include conditional text for 'no' radio button" in {

        val doc: Document = Jsoup.parse(conditionalRadio(testForm).toString)

        doc.getElementById("conditional-haveYouRegisteredPartnership.No").text mustBe "A pdf form will open that you will need to fill in and send back to us before you can add this tax to your account."

      }

      "not include conditional text for 'yes' radio button" in {

        val doc: Document = Jsoup.parse(conditionalRadio(testForm).toString)

        doc.getElementById("conditional-haveYouRegisteredPartnership.Yes") mustBe null

      }

    }
  }
