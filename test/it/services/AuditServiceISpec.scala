package services

import models.sa.CredIdFound
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.{MustMatchers, WordSpec}
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import service.AuditService
import support.AddTaxesIntegrationTest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceISpec extends WordSpec with MustMatchers with AddTaxesIntegrationTest  {

  override protected def config: Map[String, Any] = Map("auditing.enabled" -> "true")

  val testService: AuditService = inject[AuditService]
  val expected: AuditResult = AuditResult.Success

  "auditSA" should {

    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      val result = testService.auditSA("credId123", "utr321", enrolmentCheckResult = CredIdFound)

      whenReady(result){ _ mustBe expected }
    }
  }

}
