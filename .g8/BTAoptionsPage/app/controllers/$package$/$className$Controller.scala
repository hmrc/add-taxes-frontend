package controllers.$package$

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Call
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}

import forms.$package$.$className$FormProvider
import identifiers.$className$Id
import views.html.$package$.$className;format="decap"$

import scala.concurrent.Future

class $className$Controller @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        navigator: Navigator[Call],
                                        authenticate: AuthAction,
                                        serviceInfoData: ServiceInfoAction,
                                        formProvider: $className$FormProvider) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) {
    implicit request =>
      Ok($className;format="decap"$(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          BadRequest($className;format="decap"$(appConfig, formWithErrors)(request.serviceInfoContent)),
        (value) =>
          Redirect(navigator.nextPage($className$Id, value))
      )
  }
}
