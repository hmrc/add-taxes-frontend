package controllers.sa

import connectors.DataCacheConnector
import controllers.Assets.Redirect
import controllers._
import forms.sa.SelectSACategoryFormProvider
import models.sa.SelectSACategory
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import service.{CredFinderService, SelectSaCategoryService}
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

import scala.concurrent.Future

class SelectSACategoryControllerITSASpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new SelectSACategoryFormProvider()
  val form: Form[SelectSACategory] = formProvider()
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockSaCategoryService: SelectSaCategoryService = mock[SelectSaCategoryService]
  val mockCredFinderService: CredFinderService = mock[CredFinderService]
  val view: selectSACategory = injector.instanceOf[selectSACategory]
  val btaOrigin: String = "bta-sa"

  val radioButtonOptions: Set[RadioOption] = SelectSACategory.options.filterNot(_.value == SelectSACategory.MtdIT.toString)

  def controller()(enrolmentTypes: HmrcEnrolmentType*): SelectSACategoryController = {
    new SelectSACategoryController(
      frontendAppConfig,
      mcc,
      FakeAuthAction,
      FakeServiceInfoAction(enrolmentTypes: _*),
      formProvider,
      view,
      mockSaCategoryService,
      mockCredFinderService,
      mockDataCacheConnector
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

      val result = controller()().onPageLoadHasUTR(Some(btaOrigin))(fakeRequest)

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a redirect when the method onPageLoadHasUtr is invoked with origin not defined" in {

      when(mockCredFinderService.redirectSACategory(any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))

      val result = controller()().onPageLoadHasUTR(None)(fakeRequest)

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a redirect when the method onPageLoadNoUtr is invoked" in {

      when(mockCredFinderService.redirectSACategory(any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(Redirect(onwardRoute.url)))

      val result = controller()().onPageLoadNoUTR()(fakeRequest)

      status(result) mustBe SEE_OTHER

      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    /*
    "redirect to the next page when valid data is submitted" in {

      when(mockDataCacheConnector.getEntry[Boolean](any(), any())(any())).thenReturn(Future.successful(Some(true)))
      when(mockDataCacheConnector.remove(any(), any())).thenReturn(Future.successful(true))
      when(mockSaCategoryService.saResult(any(), any(), any())(any())).thenReturn(Redirect(onwardRoute.url))

      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", radioButtonOptions.head.value))
      val result = controller()().onSubmitHasUTR(btaOrigin)(postRequest)

      status(result) mustBe true

      redirectLocation(result) mustBe Some(onwardRoute.url)
    }
    */


  }


}
