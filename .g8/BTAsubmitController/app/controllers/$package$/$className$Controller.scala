package controllers.$package$

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import play.api.data.Form
import utils.Navigator

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          authenticate: AuthAction,
                                          navigator: Navigator,
                                          serviceInfo: ServiceInfoAction,
                                          formProvider: $formProvider$) extends FrontendController with I18nSupport {

  val form = formProvider()

  def onPageLoad = (authenticate andThen serviceInfo) {
    implicit request =>
      ???
  }

  def onSubmit = (authenticate andThen serviceInfo).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => ???,
        (value) => Future.successful(Redirect(navigator.nextPage($nextPageId$, value)))
      )
  }
}
