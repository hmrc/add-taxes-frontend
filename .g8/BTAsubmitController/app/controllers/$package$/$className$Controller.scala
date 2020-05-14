package controllers.$package$

import javax.inject.Inject

import controllers.actions._
import config.FrontendAppConfig
import forms.$modelPackage$.$model$FormProvider
import identifiers.$className$Id
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.MessagesControllerComponents
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.Navigator

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                          mcc: MessagesControllerComponents,
                                          authenticate: AuthAction,
                                          navigator: Navigator[Call],
                                          serviceInfo: ServiceInfoAction,
                                          formProvider: $model$FormProvider) extends FrontendController(mcc)with I18nSupport {

  val form = formProvider()

  def onPageLoad = (authenticate andThen serviceInfo) {
    implicit request =>
      ???
  }

  def onSubmit = (authenticate andThen serviceInfo) {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) => ???,
        (value) => Redirect(navigator.nextPage($className$Id, value))
      )
  }
}
