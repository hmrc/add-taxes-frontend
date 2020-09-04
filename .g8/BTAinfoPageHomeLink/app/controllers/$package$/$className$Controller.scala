package controllers.$package$

import javax.inject.Inject

import play.api.i18n.I18nSupport
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import views.html.$package$.$className;format="decap"$

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(
  appConfig: FrontendAppConfig,
  mcc: MessagesControllerComponents,
  authenticate: AuthAction,
  serviceInfo: ServiceInfoAction
) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad = (authenticate andThen serviceInfo) {
    implicit request =>
      Ok($className;format="decap"$(appConfig)(request.serviceInfoContent))
  }
}
