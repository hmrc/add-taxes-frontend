package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{postRequestedFor, urlEqualTo, verify}
import controllers.Assets.{BAD_REQUEST, CREATED}
import models.sa.SaEnrolment
import support.stubs.StubEnrolmentStoreConnector.{enrolForSaPost, stubPost}

object StubTaxEnrolmentsConnector extends StubHelper {

  def withResponseForEnrolForSa(saEnrolment: SaEnrolment, utr: String, groupId: String)(status: Int, optBody: Option[String]): Unit =
    stubPost(s"/tax-enrolments/groups/$groupId/enrolments/IR-SA~UTR~$utr", status, enrolForSaPost(saEnrolment), optBody)

  def successFulEnrolForSa(saEnrolment: SaEnrolment, utr: String, groupId: String) = withResponseForEnrolForSa(saEnrolment, utr, groupId)(
    CREATED, None
  )

  def unsuccessFulEnrolForSa(saEnrolment: SaEnrolment, utr: String, groupId: String) = withResponseForEnrolForSa(saEnrolment, utr, groupId)(
    BAD_REQUEST, None
  )

  def verifyEnrolForSa(count: Int, groupId: String, utr: String): Unit =
    verify(count, postRequestedFor(urlEqualTo(s"/tax-enrolments/groups/$groupId/enrolments/IR-SA~UTR~$utr")))
}
