package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{postRequestedFor, urlEqualTo, verify}
import models.sa.SaEnrolment
import play.api.http.Status._
import support.stubs.StubEnrolmentStoreConnector.enrolForSaPost

object StubOssConnector extends StubHelper {

  def withResponseForOssRegistrationLink(postBody: String)(status: Int, optBody: Option[String]): Unit =
    stubPost(s"/external-entry", status, postBody, optBody)

  def successFulOssRegistrationLink(postBody: String, returnBody: String) = withResponseForOssRegistrationLink(postBody)(
    CREATED, Some(returnBody)
  )

  def unsuccessFulOssRegistrationLink(postBody: String) = withResponseForOssRegistrationLink(postBody)(
    BAD_REQUEST, None
  )

  def conflictOssRegistrationLink(postBody: String) = withResponseForOssRegistrationLink(postBody)(
    CONFLICT, None
  )

  def verifyOssRegistrationLink(count: Int): Unit =
    verify(count, postRequestedFor(urlEqualTo(s"/external-entry")))
}
