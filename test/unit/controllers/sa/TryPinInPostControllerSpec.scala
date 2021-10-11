package controllers.sa

import controllers.Assets.{InternalServerError, Redirect}
import controllers.ControllerSpecBase
import handlers.ErrorHandler
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, status, _}
import play.twirl.api.HtmlFormat
import playconfig.featuretoggle.FeatureToggleSupport
import service.TryPinInPostService
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import views.html.sa.tryPinInPost

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TryPinInPostControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  val mockTryPinInPostService: TryPinInPostService = mock[TryPinInPostService]
  val view: tryPinInPost = injector.instanceOf[tryPinInPost]
  val errorHandler: ErrorHandler = injector.instanceOf[ErrorHandler]
  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty)
  val btaOrigin: String = "bta-sa"

  def controller(): TryPinInPostController = {
    new TryPinInPostController(
      frontendAppConfig,
      FakeAuthAction,
      FakeServiceInfoAction,
      mcc,
      view,
      mockTryPinInPostService
    )
  }

  def viewAsString(origin: String, status: Option[String] = Some("Failed")): String =
    new tryPinInPost(formWithCSRF, mainTemplate)(frontendAppConfig, status, origin)(HtmlFormat.empty)(fakeRequest, messages).toString

  "TryPinInPost Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(origin = btaOrigin)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(btaOrigin)
    }

    "redirect when valid sa utr is submitted and service returns unsuccessful enrolment" in {
      when(mockTryPinInPostService.checkEnrol(any())(any(), any(), any()))
        .thenReturn(Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0123456789"))

      val result = controller().onSubmit(origin = btaOrigin)(postRequest)

      status(result) mustBe INTERNAL_SERVER_ERROR
    }

    "redirect when valid sa utr is submitted and service returns successful request" in {
      when(mockTryPinInPostService.checkEnrol(any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(controllers.sa.routes.RequestedAccessController.onPageLoad(btaOrigin))))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0123456789"))

      val result = controller().onSubmit(origin = btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).get mustBe controllers.sa.routes.RequestedAccessController.onPageLoad(btaOrigin).url
    }
  }

}
