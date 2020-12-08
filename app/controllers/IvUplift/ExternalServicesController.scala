package controllers.IvUplift

import connectors.DataCacheConnector
import controllers.actions.{AuthAction, ServiceInfoAction}
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.sa.{KnownFacts, SAUTR}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import service.KnownFactsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

class ExternalServicesController @Inject()(mcc: MessagesControllerComponents,
                                           authenticate: AuthAction,
                                           serviceInfoData: ServiceInfoAction,
                                          knownFactsService: KnownFactsService,
                                          dataCacheConnector: DataCacheConnector)
  extends FrontendController(mcc) with I18nSupport {

  implicit val ec: ExecutionContext = mcc.executionContext

  def upliftJourney(utr: String, nino: String): Action[AnyContent] = (authenticate andThen serviceInfoData).async {
    implicit request =>
      dataCacheConnector.save[SAUTR](request.request.credId, EnterSAUTRId.toString, SAUTR(utr))
      val knownFacts = KnownFacts(None, nino = Some(nino), None)
      knownFactsService.knownFactsLocation(knownFacts, upliftJourney = true)
  }
}