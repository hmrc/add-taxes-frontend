package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{postRequestedFor, urlEqualTo, verify}
import config.FrontendAppConfig
import play.api.http.Status._

object StubOssConnector extends StubHelper {

  def withResponseForOssRegistrationLink(postBody: String)(status: Int, optBody: Option[String])(implicit appConfig: FrontendAppConfig): Unit =
    stubPost(appConfig.vatOssExternalEntryUrl, status, postBody, optBody)

  def successFulOssRegistrationLink(postBody: String, returnBody: String)(implicit appConfig: FrontendAppConfig) = withResponseForOssRegistrationLink(postBody)(
    CREATED, Some(returnBody)
  )

  def unsuccessFulOssRegistrationLink(postBody: String)(implicit appConfig: FrontendAppConfig) = withResponseForOssRegistrationLink(postBody)(
    BAD_REQUEST, None
  )

  def conflictOssRegistrationLink(postBody: String)(implicit appConfig: FrontendAppConfig) = withResponseForOssRegistrationLink(postBody)(
    CONFLICT, None
  )

  def verifyOssRegistrationLink(count: Int)(implicit appConfig: FrontendAppConfig): Unit =
    verify(count, postRequestedFor(urlEqualTo(appConfig.vatOssExternalEntryUrl)))
}
