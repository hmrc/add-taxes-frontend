package views.employer

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.directorsRegisterByPhone

class DirectorsRegisterByPhoneViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "directorsRegisterByPhone"

  def createView = () => directorsRegisterByPhone(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "DirectorsRegisterByPhone view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }
}
