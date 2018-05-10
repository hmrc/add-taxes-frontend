package views.vat.moss.uk

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.uk.addVATFirst

class AddVATFirstViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "addVATFirst"

  def createView = () => addVATFirst(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "AddVATFirst view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc =  asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Add VAT to this account",
        "???",
        "VatMossUkAddVatToAccount:Click:AddVat"
      )

      assertLinkById(
        doc,
        "not-now",
        "I don't want to do this right now",
        "http://localhost:9020/business-account",
        "VatMossUkAddVatToAccount:Click:NotNow"
      )
    }
  }
}
