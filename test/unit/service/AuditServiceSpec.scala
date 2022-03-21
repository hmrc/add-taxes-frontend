package service

import models.other.importexports.DoYouWantToAddImportExport
import models.sa._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import uk.gov.hmrc.http.{HeaderCarrier, RequestId, SessionId}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import utils.HmrcEnrolmentType
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditServiceSpec extends PlaySpec with MockitoSugar {

  val mockAuditConnector: AuditConnector = mock[AuditConnector]

  val testService = new AuditService(mockAuditConnector)
  val expected: AuditResult = AuditResult.Success
  val saEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(), state = "Activated")

  "auditSA" should {

    implicit val hc: HeaderCarrier = HeaderCarrier(
      requestId = Some(RequestId("testId")),
      sessionId = Some(SessionId("testId2")),
      trueClientIp = Some("testIp"),
      trueClientPort = Some("testPort")
    )
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(expected))

      val result = testService.auditSA("credId123", "utr321", enrolmentCheckResult = CredIdFound)

      whenReady(result){ _ mustBe expected }
    }
  }

  "auditSAKnownfacts" should {

    implicit val hc: HeaderCarrier = HeaderCarrier(
      requestId = Some(RequestId("testId")),
      sessionId = Some(SessionId("testId2")),
      trueClientIp = Some("testIp"),
      trueClientPort = Some("testPort")
    )
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      val knownFacts = new KnownFacts(Some("AB112233D"), None, None)

      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(expected))

      val result = testService.auditSAKnownFacts("credId123", "utr321", knownFacts, knownfactsResult = true)

      whenReady(result){ _ mustBe expected }
    }

    "successfully audit with None Knownfacts" in {
      val knownFacts = new KnownFacts(None, None, None)

      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(expected))

      val result = testService.auditSAKnownFacts("credId123", "utr321", knownFacts, knownfactsResult = true)

      whenReady(result){ _ mustBe expected }
    }
  }

  "auditEPAYE" should {

    implicit val hc: HeaderCarrier = HeaderCarrier(
      requestId = Some(RequestId("testId")),
      sessionId = Some(SessionId("testId2")),
      trueClientIp = Some("testIp"),
      trueClientPort = Some("testPort")
    )
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(expected))

      val result = testService.auditEPAYE("credId123", "ref321", recordMatch = true)

      whenReady(result){ _ mustBe expected }
    }
  }
  "auditSelectSACategory" should {
    implicit val hc: HeaderCarrier = HeaderCarrier(
      requestId = Some(RequestId("testId")),
      sessionId = Some(SessionId("testId2")),
      trueClientIp = Some("testIp"),
      trueClientPort = Some("testPort")
    )
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(expected))

      val result = testService.auditSelectSACategory(saType = SelectSACategory.Sa, doYouHaveSaUtr = DoYouHaveSAUTR.Yes, "utr123", "credId123", "groupId123")

      whenReady(result){ _ mustBe expected }
    }
  }

  "auditSelectIOCategory" should {
    implicit val hc: HeaderCarrier = HeaderCarrier(
      requestId = Some(RequestId("testId")),
      sessionId = Some(SessionId("testId2")),
      trueClientIp = Some("testIp"),
      trueClientPort = Some("testPort")
    )
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(expected))
      val enrolments: Enrolments = Enrolments(Set(saEnrolment))

      val result = testService.auditSelectIOCategory("credId123", DoYouWantToAddImportExport.ATaR, enrolments)

      whenReady(result){ _ mustBe expected }
    }
  }
}
