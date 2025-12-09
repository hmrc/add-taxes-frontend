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

  val testService                          = new AuditService(mockAuditConnector)
  private val successResponse: AuditResult = AuditResult.Success
  private val failureResponse: AuditResult = AuditResult.Failure("Event was actively rejected")
  val saEnrolment: Enrolment               = Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(), state = "Activated")

  implicit val hc: HeaderCarrier = HeaderCarrier(
    requestId = Some(RequestId("testId")),
    sessionId = Some(SessionId("testId2")),
    trueClientIp = Some("testIp"),
    trueClientPort = Some("testPort")
  )

  "auditSA" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(successResponse))

      val result = testService.auditSA("credId123", "utr321", enrolmentCheckResult = CredIdFound)

      whenReady(result)(_ mustBe successResponse)
    }
  }

  "auditSAKnownfacts" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      val knownFacts = new KnownFacts(Some("AB112233D"), None, None)

      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(successResponse))

      val result = testService.auditSAKnownFacts("credId123", "utr321", knownFacts, knownfactsResult = true)

      whenReady(result)(_ mustBe successResponse)
    }

    "successfully audit with None Knownfacts" in {
      val knownFacts = new KnownFacts(None, None, None)

      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(successResponse))

      val result = testService.auditSAKnownFacts("credId123", "utr321", knownFacts, knownfactsResult = true)

      whenReady(result)(_ mustBe successResponse)
    }
  }

  "auditEPAYE" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(successResponse))

      val result = testService.auditEPAYE("credId123", "ref321", recordMatch = true)

      whenReady(result)(_ mustBe successResponse)
    }
  }

  "auditSelectSACategory" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(successResponse))

      val result =
        testService.auditSelectSACategory(saType = SelectSACategory.Sa, doYouHaveSaUtr = DoYouHaveSAUTR.Yes, "utr123", "credId123", "groupId123")

      whenReady(result)(_ mustBe successResponse)
    }
  }

  "auditSelectIOCategory" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "successfully audit" in {
      when(mockAuditConnector.sendEvent(any())(any(), any()))
        .thenReturn(Future.successful(successResponse))
      val enrolments: Enrolments = Enrolments(Set(saEnrolment))

      val result = testService.auditSelectIOCategory("credId123", DoYouWantToAddImportExport.ATaR, enrolments)

      whenReady(result)(_ mustBe successResponse)
    }
  }

  "auditCveMultipleVrnsAttempted" should {
    implicit val request: FakeRequest[AnyContent] = FakeRequest()

    "return an Audit Success response from the controller" in {
      when(mockAuditConnector.sendEvent(any())(any(), any())).thenReturn(Future.successful(successResponse))

      val result = testService.auditCveMultipleVrnsAttempted(originalVatNumber = "123456789", newVatNumber = "987654321")

      whenReady(result)(_ mustBe successResponse)
    }

    "return an Audit Failure response from the controller" in {
      when(mockAuditConnector.sendEvent(any())(any(), any())).thenReturn(Future.successful(failureResponse))

      val result = testService.auditCveMultipleVrnsAttempted(originalVatNumber = "123456789", newVatNumber = "987654321")

      whenReady(result)(_ mustBe failureResponse)
    }
  }

}
