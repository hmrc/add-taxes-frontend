package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object StubServiceInfoPartialConnector {

  def withResponseForNavLinks()(status: Int, optBody: Option[String]): Unit =
    stubFor(get(urlEqualTo(s"/business-account/partial/nav-links")) willReturn {
      val coreResponse = aResponse().withStatus(status)
      optBody match {
        case Some(body) => coreResponse.withBody(body)
        case _          => coreResponse
      }
    })

  def verifyNavlinksContent(count: Int): Unit =
    verify(count, getRequestedFor(urlMatching(s"/business-account/partial/nav-links")))
}
