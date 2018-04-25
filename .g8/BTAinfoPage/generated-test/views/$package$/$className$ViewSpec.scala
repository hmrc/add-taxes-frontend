package views.$package$

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.$package$.$className;format="decap"$

class $className$ViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "$className;format="decap"$"

  def createView = () => $className;format="decap"$(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "$className$ view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }
}
