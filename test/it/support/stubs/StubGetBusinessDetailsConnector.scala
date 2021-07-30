package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object StubGetBusinessDetailsConnector extends StubHelper {

  def withResponseGetBusinessDetails(nino: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(s"/registration/business-details/nino/$nino", status, optBody)

  def verifyGetBusinessDetails(nino: String, count: Int): Unit =
    verify(count, getRequestedFor(urlMatching(s"/registration/business-details/nino/$nino")))
}
