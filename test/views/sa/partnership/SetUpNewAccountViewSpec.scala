package views.sa.partnership

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.sa.partnership.setUpNewAccount

class SetUpNewAccountViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "setUpNewAccount"

  def createView = () => setUpNewAccount(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "SetUpNewAccount view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc =  asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Sign out and create  ''Organisation'' account",
        "???",
        "SetUpAccountpartneriaethHunanasesiad:Click:Register"
      )

      assertLinkById(
        doc,
        "not-now",
        "I don’t want to do this right now",
        "http://localhost:9020/business-account",
        "SetUpAccountpartneriaethHunanasesiad:Click:NotNow"
      )
    }
  }
}
