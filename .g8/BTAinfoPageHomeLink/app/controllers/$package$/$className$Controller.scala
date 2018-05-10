package controllers.$package$

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import views.html.$package$.$className;format="decap"$

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  serviceInfo: ServiceInfoAction
) extends FrontendController with I18nSupport {

  def onPageLoad = (authenticate andThen serviceInfo) {
    implicit request =>
      Ok($className;format="decap"$(appConfig)(request.serviceInfoContent))
  }
}
