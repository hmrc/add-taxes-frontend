package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.http.Status._

object StubVatSubscriptionConnector extends StubHelper {

  def withResponseMandationStatus(vrn: String)(status: Int, optBody: Option[String] = None): Unit = {
    val response = aResponse().withStatus(status)
    val finalResponse = optBody match {
      case Some(body) => response.withBody(body)
      case None => response
    }
    stubFor(get(urlEqualTo(s"/vat-subscription/$vrn/mandation-status")).willReturn(finalResponse))
  }

  def successMandationStatus(vrn: String): Unit =
    withResponseMandationStatus(vrn)(OK, None)

  def notFoundMandationStatus(vrn: String): Unit =
    withResponseMandationStatus(vrn)(NOT_FOUND, None)

  def preconditionFailedMandationStatus(vrn: String): Unit =
    withResponseMandationStatus(vrn)(PRECONDITION_FAILED, None)

  def serverErrorMandationStatus(vrn: String): Unit =
    withResponseMandationStatus(vrn)(INTERNAL_SERVER_ERROR, None)

  def invalidJsonMandationStatus(vrn: String): Unit =
    withResponseMandationStatus(vrn)(OK, Some("""{ invalid-json }"""))

  def throwExceptionMandationStatus(vrn: String): Unit =
    stubFor(get(urlEqualTo(s"/vat-subscription/$vrn/mandation-status"))
      .willReturn(aResponse().withStatus(500)))

  def verifyMandationStatusCalled(count: Int, vrn: String): Unit =
    verify(count, getRequestedFor(urlEqualTo(s"/vat-subscription/$vrn/mandation-status")))
}