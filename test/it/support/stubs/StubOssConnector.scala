package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{postRequestedFor, urlEqualTo, verify}
import config.FrontendAppConfig
import play.api.http.Status._

object StubOssConnector extends StubHelper {

  def withResponseForOneStopRegistrationLink(postBody: String)(status: Int,
                                                           optBody: Option[String],
                                                           iossBool: Boolean,
                                                           lang: String)(implicit appConfig: FrontendAppConfig): Unit = {
    val serviceUrl = if (iossBool) appConfig.vatIossExternalEntryUrl + s"?lang=$lang" else appConfig.vatOssExternalEntryUrl + s"?lang=$lang"
    stubPost(serviceUrl, status, postBody, optBody)
  }

  def successFulOneStopRegistrationLink(postBody: String,
                                    returnBody: String,
                                    iossBool: Boolean,
                                    lang: String)(implicit appConfig: FrontendAppConfig) = withResponseForOneStopRegistrationLink(postBody)(
      CREATED, Some(returnBody), iossBool, lang
    )

  def unsuccessFulOneStopRegistrationLink(postBody: String,
                                      iossBool: Boolean,
                                      lang: String)(implicit appConfig: FrontendAppConfig) = withResponseForOneStopRegistrationLink(postBody)(
    BAD_REQUEST, None, iossBool, lang
  )

  def conflictOneStopRegistrationLink(postBody: String,
                                  iossBool: Boolean,
                                  lang: String)(implicit appConfig: FrontendAppConfig) = withResponseForOneStopRegistrationLink(postBody)(
    CONFLICT, None, iossBool, lang
  )

  def verifyOneStopRegistrationLink(count: Int,
                                iossBool: Boolean,
                                lang: String)(implicit appConfig: FrontendAppConfig): Unit = {
    val serviceUrl = if (iossBool) appConfig.vatIossExternalEntryUrl + s"?lang=$lang" else appConfig.vatOssExternalEntryUrl + s"?lang=$lang"
    verify(count, postRequestedFor(urlEqualTo(serviceUrl)))
  }
}
