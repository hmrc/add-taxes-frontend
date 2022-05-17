package controllers.sa
import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.IvUpliftSwitch
import config.featureToggles.FeatureToggleSupport
import connectors.DataCacheConnector
import controllers.ControllerSpecBase
import models.sa.SAUTR
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers._
import service.SaService

import scala.concurrent.Future

class TryIvControllerSpec extends ControllerSpecBase with MockitoSugar with FeatureToggleSupport {

  val mockSaService: SaService = mock[SaService]
  val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
  val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val btaOrigin: String = "bta-sa"

  def controller(): TryIvController =
    new TryIvController(
      FakeAuthAction,
      FakeServiceInfoAction,
      mcc,
      mockSaService,
      mockDataCacheConnector,
      mockAppConfig
    )

  "TryIVController" when {
      "redirect back to TryPinInPost when no UTR is found" in {
        disable(IvUpliftSwitch)
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(None)
        val result = controller().onPageLoad(btaOrigin)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/try-pin-in-post?status=MatchingError&origin=bta-sa")
      }

      "redirect back to the IV link returned from the connector when UTR is found" in {
        disable(IvUpliftSwitch)
        when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567890")))
        when(mockSaService.getIvRedirectLink(any(), any())(any(), any(), any())) thenReturn(Future.successful("/iv-link"))
        val result = controller().onPageLoad(btaOrigin)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some("/iv-link")
      }

    "redirect back to the IV link returned from the connector when UTR is found and feature is true" in {
      enable(IvUpliftSwitch)
      when(mockAppConfig.ivUpliftUrl(any())).thenReturn("testLink")
      when(mockDataCacheConnector.getEntry[SAUTR](any(), any())(any())) thenReturn Future.successful(Some(SAUTR("1234567890")))
      val result = controller().onPageLoad(btaOrigin)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("testLink")
    }
  }
}
