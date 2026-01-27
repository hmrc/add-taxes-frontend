package controllers.sa

import connectors.DataCacheConnector
import controllers.ControllerSpecBase
import forms.sa.CaptureSAUTRFormProvider
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{CaptureSAUTRModel, DoYouHaveSAUTR, SAUTR}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.internal.verification.VerificationModeFactory.times
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.api.http.Status._
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import play.twirl.api.HtmlFormat
import service.SelectSaCategoryService
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.Enrolments
import views.html.sa.captureSAUTR

import scala.concurrent.Future

class CaptureSAUTRControllerSpec extends ControllerSpecBase with BeforeAndAfterEach {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad


  implicit val request: ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
    AuthenticatedRequest(FakeRequest(), "", Enrolments(Set()), Some(Individual), groupId, providerId, confidenceLevel, None),
    HtmlFormat.empty
  )

  val formProvider = new CaptureSAUTRFormProvider()
  val form: Form[CaptureSAUTRModel] = formProvider()

  val view: captureSAUTR = injector.instanceOf[captureSAUTR]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockSaCategoryService: SelectSaCategoryService = mock[SelectSaCategoryService]


  def controller(): CaptureSAUTRController = {
    new CaptureSAUTRController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction,
      formProvider,
      view,
      mockDataCacheConnector,
      mockSaCategoryService
    )
  }

  override def beforeEach(): Unit = {
    reset(mockDataCacheConnector)
  }

  def viewAsString(form: Form[_] = form): String =
    new captureSAUTR(formWithCSRF, mainTemplate)(frontendAppConfig, form)(HtmlFormat.empty)(fakeRequest, messages).toString

  "CaptureSAUTR Controller" must {

    when(mockDataCacheConnector.remove(any(), any())).thenReturn(Future.successful(true))

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad()(fakeRequest.withMethod("GET"))

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to the next page when valid data (yes, valid sautr) is submitted" in {
      when(mockDataCacheConnector.save[SAUTR](any(), any(), any())(any())).thenReturn(Future.successful(emptyCacheMap))
      when(mockDataCacheConnector.getEntry[Boolean](any(),any())(any())).thenReturn(Future.successful(Some(true)))
      when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveSAUTR.options.head.value),("sautrValue","1234567890")).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment?origin=bta-sa")
      verify(mockDataCacheConnector, times(1)).remove(any(), any())
    }

    "redirect to the next page when invalid data (yes, 123ABC) is submitted" in {
      when(mockDataCacheConnector.save[SAUTR](any(), any(), any())(any())).thenReturn(Future.successful(emptyCacheMap))
      when(mockDataCacheConnector.getEntry[Boolean](any(),any())(any())).thenReturn(Future.successful(Some(true)))
      when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", DoYouHaveSAUTR.options.head.value),("sautrValue","123ABC")).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe BAD_REQUEST
    }

    "redirect to the next page when No utr option submitted" in {
      when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "No")).withMethod("POST")

      val result = controller().onSubmit()(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/no-utr")
      verify(mockDataCacheConnector, times(1)).remove(any(), any())
    }

  }
}
