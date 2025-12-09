package services

import models.sa.CredIdFound
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
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
      val result = testService.auditSA("credId123", "utr321", enrolmentCheckResult = CredIdFound)

      whenReady(result)(_ mustBe expected)
    }
  }

  "auditCveMultipleVrnsAttempted" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      val result = testService.auditCveMultipleVrnsAttempted(originalVatNumber = "123456789", newVatNumber = "987654321")

      whenReady(result)(_ mustBe expected)
    }
  }

}
