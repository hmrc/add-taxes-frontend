package connectors
import config.FrontendAppConfig
import javax.inject.Inject
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrolmentStoreProxyConnector @Inject()(appConfig: FrontendAppConfig, http: HttpGet, logger: Logger) {

  def checkExistingUTR(utr: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    http.GET(appConfig.checkUtrUrl(utr)).map { response =>
      response.status match {
        case OK => true
        case NO_CONTENT => false
        case status =>
          Logger.error(s"Enrolment Store Proxy returned status code: $status, body: ${response.body}")
          false
      }
    } recover { case exception =>
      Logger.error("Enrolment Store Proxy error", exception)
      false
    }
  }
}