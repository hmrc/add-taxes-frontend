package services

import models.sa.CredIdFound
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import service.AuditService
import support.AddTaxesIntegrationTest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceISpec extends PlaySpec with AddTaxesIntegrationTest {

  override protected def config: Map[String, Any] = Map("auditing.enabled" -> "true")

  val testService: AuditService = inject[AuditService]
  val expected: AuditResult     = AuditResult.Success

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "auditSA" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      val result = await(testService.auditSA("credId123", "utr321", enrolmentCheckResult = CredIdFound))

      result mustBe expected
    }
  }

  "auditCveMultipleVrnsAttempted" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      val result =
        await(testService.auditCveMultipleVrnsAttempted(originalVatNumber = "123456789", newVatNumber = "987654321", userType = "Organisation"))

      result mustBe expected
    }
  }

}
