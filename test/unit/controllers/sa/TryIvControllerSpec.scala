package controllers.sa
import connectors.DataCacheConnector
import controllers.ControllerSpecBase
import models.sa.SAUTR
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import playconfig.featuretoggle.FeatureToggleSupport
import service.SaService

import scala.concurrent.Future

class TryIvControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  val mockSaService = mock[SaService]
  val mockDataCacheConnector = mock[DataCacheConnector]
  val btaOrigin: String = "bta-sa"

  def controller(featureToggle: Boolean = true): TryIvController =
    new TryIvController(
      FakeAuthAction,
      FakeServiceInfoAction,
      frontendAppConfig,
      mcc,
      mockSaService,
      mockDataCacheConnector
    ) {
      override val pinAndPostFeatureToggle = featureToggle
    }

  "TryIVController" when {
    "pinInPost feature toggle is set to false" must {
      "redirect to the BTA homepage" in {
        val result = controller(false).onPageLoad(btaOrigin)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("http://localhost:9020/business-account")
      }
    }

    "pinInPost feature toggle is set to true" must {
      "redirect back to TryPinInPost when no UTR is found" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        val result = controller().onPageLoad(btaOrigin)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/try-pin-in-post?status=MatchingError&origin=bta-sa")
      }

      "redirect back to the IV link returned from the connector when UTR is found" in {
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567890")))
        when(mockSaService.getIvRedirectLink(any(), any())(any(), any(), any())) thenReturn(Future.successful("/iv-link"))
        val result = controller().onPageLoad(btaOrigin)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/iv-link")
      }
    }
  }
}
