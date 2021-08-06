package support.stubs

import play.api.test.Helpers._

object StubCitizenDetailsConnector extends StubHelper {

  def withResponseForGetDesignatoryDetails(utr: String)(status: Int, body: Option[String]): Unit = {
    stubGet(s"/citizen-details/sautr/$utr", status, body)
  }

  def stubDesignatoryDetailsNotFound(utr: String): Unit = {
    withResponseForGetDesignatoryDetails(utr)(NOT_FOUND,
      None)
  }

  def stubDesignatoryDetailsBadRequest(utr: String): Unit = {
    withResponseForGetDesignatoryDetails(utr)(BAD_REQUEST,
      None)
  }

  def stubDesignatoryDetailsInternalServerError(utr: String): Unit = {
    withResponseForGetDesignatoryDetails(utr)(INTERNAL_SERVER_ERROR,
      None)
  }

}
