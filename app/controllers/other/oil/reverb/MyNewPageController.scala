package controllers.other.oil.reverb

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enumerable, Navigator}

import forms.other.oil.reverb.MyNewPageFormProvider
import identifiers.MyNewPageId
import views.html.other.oil.reverb.myNewPage

import scala.concurrent.Future

class MyNewPageController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        authenticate: AuthAction,
                                        serviceInfoData: ServiceInfoAction,
                                        formProvider: MyNewPageFormProvider) extends FrontendController with I18nSupport with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) {
    implicit request =>
      Ok(myNewPage(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(myNewPage(appConfig, formWithErrors)(request.serviceInfoContent))),
        (value) =>
          Future.successful(Redirect(navigator.nextPage(MyNewPageId, value)))
      )
  }
}
