package controllers.sa

import connectors.DataCacheConnector
import controllers._
import forms.sa.SelectSACategoryFormProvider
import models.sa.SelectSACategory
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.mvc.Results.Redirect
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.{CredFinderService, SelectSaCategoryService}
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

import scala.concurrent.Future

class SelectSACategoryControllerITSASpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad

  val formProvider = new SelectSACategoryFormProvider()
  val form: Form[SelectSACategory] = formProvider()
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockSaCategoryService: SelectSaCategoryService = mock[SelectSaCategoryService]
  val mockCredFinderService: CredFinderService = mock[CredFinderService]
  val view: selectSACategory = injector.instanceOf[selectSACategory]
  val btaOrigin: String = "bta-sa"
  val invalidValue: String = "invalid value"

  val radioButtonOptions: Set[RadioOption] = SelectSACategory.options.filterNot(_.value == SelectSACategory.MtdIT.toString)

  def controller()(enrolmentTypes: HmrcEnrolmentType*): SelectSACategoryController = {
    new SelectSACategoryController(
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider,
      view,
      mockSaCategoryService,
      mockCredFinderService,
      mockDataCacheConnector,
      frontendAppConfig
    ) {override val accessMtdFeatureSwitch: Boolean = true}
  }

  def viewAsString(form: Form[_] = form, radioOptions: Set[RadioOption] = radioButtonOptions, origin: String): String =
    new selectSACategory(
      formWithCSRF, mainTemplate
    )(frontendAppConfig, form, routes.SelectSACategoryController.onSubmitHasUTR(origin), origin, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  def viewAsStringNoUTR(form: Form[_] = form, radioOptions: Set[RadioOption] = radioButtonOptions, origin: String): String =
    new selectSACategory(
      formWithCSRF, mainTemplate
    )(frontendAppConfig, form, routes.SelectSACategoryController.onSubmitNoUTR(), origin, radioOptions)(HtmlFormat.empty)(fakeRequest, messages).toString

  override def beforeEach(): Unit = {
    reset(mockCredFinderService)
    reset(mockDataCacheConnector)
    reset(mockSaCategoryService)
  }

  "SelectSACategory Controller" must {

    "return a redirect when the method onPageLoadHasUtr is invoked with origin defined" in {

      when(mockCredFinderService.redirectSACategory(any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))

      val result = controller()().onPageLoadHasUTR(Some(btaOrigin))(fakeRequest.withMethod("GET"))

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)

      verify(mockCredFinderService, times(1))
        .redirectSACategory(any(), any(), any())(any(), any(), any(), any())
    }

    "return a redirect when the method onPageLoadHasUtr is invoked with origin not defined" in {

      when(mockCredFinderService.redirectSACategory(any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))

      val result = controller()().onPageLoadHasUTR(None)(fakeRequest.withMethod("GET"))

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)

      verify(mockCredFinderService, times(1))
        .redirectSACategory(any(), any(), any())(any(), any(), any(), any())
    }

    "return a redirect when the method onPageLoadNoUtr is invoked" in {

      when(mockCredFinderService.redirectSACategory(any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))

      val result = controller()().onPageLoadNoUTR()(fakeRequest.withMethod("GET"))

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)

      verify(mockCredFinderService, times(1))
        .redirectSACategory(any(), any(), any())(any(), any(), any(), any())
    }

    "redirect to the next page when valid data is submitted" in {

      when(mockDataCacheConnector.getEntry[Boolean](any(), any())(any())).thenReturn(Future.successful(Some(true)))
      when(mockDataCacheConnector.remove(any(), any())).thenReturn(Future.successful(true))
      when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", radioButtonOptions.head.value)).withMethod("POST")
      val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)

      verify(mockDataCacheConnector, times(1)).getEntry[Boolean](any(), any())(any())
      verify(mockDataCacheConnector, times(1)).remove(any(), any())
      verify(mockSaCategoryService, times(1)).saCategoryResult(any(), any(), any())(any(), any(), any())
    }

    "return a bad request and errors when invalid data is submitted" in {

      when(mockDataCacheConnector.getEntry[Boolean](any(), any())(any())).thenReturn(Future.successful(Some(true)))
      when(mockDataCacheConnector.remove(any(), any())).thenReturn(Future.successful(true))

      when(mockCredFinderService.getRadioOptions(any(), any())).thenReturn(radioButtonOptions)

      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> invalidValue).withMethod("POST")

      val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

      status(result) mustBe BAD_REQUEST

      val boundForm = form.bind(Map("value" -> invalidValue))

      contentAsString(result) mustBe viewAsString(boundForm, origin = btaOrigin)

      verify(mockDataCacheConnector, times(1)).getEntry[Boolean](any(), any())(any())
      verify(mockDataCacheConnector, times(1)).remove(any(), any())
      verify(mockCredFinderService, times(1)).getRadioOptions(any(), any())
    }

    for(option <- radioButtonOptions){
      s"redirect to next page when '${option.value}' is submitted" in {

        when(mockDataCacheConnector.getEntry[Boolean](any(), any())(any())).thenReturn(Future.successful(Some(true)))
        when(mockDataCacheConnector.remove(any(), any())).thenReturn(Future.successful(true))
        when(mockSaCategoryService.saCategoryResult(any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Redirect(onwardRoute.url)))

        val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> option.value).withMethod("POST")
        val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(onwardRoute.url)

        verify(mockDataCacheConnector, times(1)).getEntry[Boolean](any(), any())(any())
        verify(mockDataCacheConnector, times(1)).remove(any(), any())
        verify(mockSaCategoryService, times(1)).saCategoryResult(any(), any(), any())(any(), any(), any())
      }
    }

  }

}
