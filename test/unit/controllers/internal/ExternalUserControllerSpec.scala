package controllers.internal

import controllers.ControllerSpecBase
import models.requests.AuthenticatedRequest
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.IvService
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments

import scala.concurrent.Future

class ExternalUserControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockIvService: IvService = mock[IvService]

  implicit val request: Request[_] = Request(
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  val  validJsonWithUtr: JsValue = Json.parse(
    s"""
       |{
       |  "origin" : "pta-sa",
       |  "utr": "1234567890",
       |  "providerId" :"123456"
       |}
       |""".stripMargin
  )

  val validJsonNoUtr: JsValue = Json.parse(
    s"""
       |{
       |  "origin" : "pta-sa",
       |  "providerId" :"123456"
       |}
       |""".stripMargin
  )

  val inValidJson: JsValue = Json.parse(
    s"""
       |{
       |  "utr": "1234567890"
       |}
       |""".stripMargin
  )

  val jsonInvalidUtr: JsValue = Json.parse(
    s"""
       |{
       |  "utr": "12345678903&",
       |  "origin" : "pta-sa",
       |  "providerId" :"123456"
       |}
       |""".stripMargin
  )

  val jsonInvalidOrigin: JsValue = Json.parse(
    s"""
       |{
       |  "utr": "1234567890",
       |  "origin" : "invalid",
       |  "providerId" :"123456"
       |}
       |""".stripMargin
  )

  val jsonBothInvalid: JsValue = Json.parse(
    s"""
       |{
       |  "utr": "12345678903&",
       |  "origin" : "invalid",
       |  "providerId" :"123456"
       |}
       |""".stripMargin
  )

  def fakeRequestWithBody(body: JsValue): FakeRequest[AnyContentAsJson] = {
    FakeRequest("POST", "").withJsonBody(body)
  }

  def controller(): ExternalUserController = {
    new ExternalUserController(
      frontendAppConfig,
      mcc,
      mockIvService)
  }

  "ExternalUserController" when {
    "initiateJourney is called" should {

      "return try-iv url as JSON when body is valid and contains a UTR" in {
        when(mockIvService.journeyRouter(any())(any())).thenReturn(Future.successful(controllers.sa.routes.TryIvController.onPageLoad("pta-sa").url))
        val result = controller().initiateJourney()(fakeRequestWithBody(validJsonWithUtr))
        status(result) mustBe OK
        contentAsJson(result) mustBe Json.parse(
          s"""
             |{
             |  "redirectUrl" : "/business-account/add-tax/self-assessment/try-iv?origin=pta-sa"
             |}
             |""".stripMargin)
      }

      "return enter-sa-utr url as JSON when JSON body is valid and does not contains a UTR" in {
        when(mockIvService.journeyRouter(any())(any())).thenReturn(Future.successful(controllers.sa.routes.EnterSAUTRController.onPageLoad(Some("pta-sa")).url))
        val result = controller().initiateJourney()(fakeRequestWithBody(validJsonNoUtr))
        status(result) mustBe OK
        contentAsJson(result) mustBe Json.parse(
          s"""
             |{
             |  "redirectUrl" : "/business-account/add-tax/self-assessment/enter-sa-utr?origin=pta-sa"
             |}
             |""".stripMargin)
      }

      "return a Bad request when JSON body is invalid" in {
        val result = controller().initiateJourney()(fakeRequestWithBody(inValidJson))
        status(result) mustBe BAD_REQUEST
      }

      "return a Bad request and appropriate message when JSON body contains invalid utr" in {
        val result = controller().initiateJourney()(fakeRequestWithBody(jsonInvalidUtr))
        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe "Invalid utr: 12345678903&"
      }

      "return a Bad request and appropriate message when JSON body contains invalid origin" in {
        val result = controller().initiateJourney()(fakeRequestWithBody(jsonInvalidOrigin))
        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe "Invalid origin: invalid"
      }

      "return a Bad request and appropriate message when JSON body contains invalid utr and origin" in {
        val result = controller().initiateJourney()(fakeRequestWithBody(jsonBothInvalid))
        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe "Invalid origin: invalid, Invalid utr: 12345678903&"
      }
    }
  }
}
