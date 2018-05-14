package controllers.$package$

import javax.inject.Inject

import controllers.actions._
import config.FrontendAppConfig
import forms.$modelPackage$
import identifiers.$className$Id
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.Navigator

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          authenticate: AuthAction,
                                          navigator: Navigator,
                                          serviceInfo: ServiceInfoAction,
                                          formProvider: $model$FormProvider) extends FrontendController with I18nSupport {

  val form = formProvider()

  def onPageLoad = (authenticate andThen serviceInfo) {
    implicit request =>
      ???
  }

  def onSubmit = (authenticate andThen serviceInfo).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => ???,
        (value) => Future.successful(Redirect(navigator.nextPage($className$Id, value)))
      )
  }
}
