package views.employer

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.partnershipRegisterByPhone

class PartnershipRegisterByPhoneViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "partnershipRegisterByPhone"

  def createView = () => partnershipRegisterByPhone(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "PartnershipRegisterByPhone view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }
}
