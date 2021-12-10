package service

import connectors.{CitizensDetailsConnector, DataCacheConnector, SaConnector}
import controllers.ControllerSpecBase
import controllers.sa.SelectSACategoryController
import forms.sa.SelectSACategoryFormProvider
import models.{BusinessDetails, DesignatoryDetails}
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.{SAUTR, SelectSACategory}
import models.sa.SelectSACategory.{MtdIT, Partnership, Sa, Trust}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.concurrent.ScalaFutures.whenReady
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory

import scala.concurrent.{ExecutionContext, Future, future}
import scala.concurrent.ExecutionContext.Implicits.global

class CredFinderServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val saEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(), state = "Activated")

    val saPartnershipsEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.Partnerships.toString, identifiers = Seq(), state = "Activated")

    val saTrustsEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.RegisterTrusts.toString, identifiers = Seq(), state = "Activated")

    val mtdITEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.MTDIT.toString, identifiers = Seq(), state = "Activated")

    val mtdBoolCheck: Boolean = false

    def test(affinityGroup: AffinityGroup, enrolments: Enrolments): ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
      AuthenticatedRequest(FakeRequest(), "", enrolments, Some(affinityGroup), groupId, providerId, confidenceLevel, None),
      HtmlFormat.empty)

    val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
    val mockCitizensDetailsConnector: CitizensDetailsConnector = mock[CitizensDetailsConnector]
    val mockSaConnector: SaConnector = mock[SaConnector]
    val mockSelectSACategory: selectSACategory = mock[selectSACategory]
    val mockCall: Call = controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR(Some("bta"))
    val mockSaCatController: SelectSACategoryController = mock[SelectSACategoryController]
    val view: selectSACategory = injector.instanceOf[selectSACategory]

  val formProvider = new SelectSACategoryFormProvider()
    val form: Form[SelectSACategory] = formProvider()

    val btaOrigin: String = "bta-sa"
    def testService: CredFinderService = new CredFinderService(
        mockCitizensDetailsConnector,
        mockSaConnector,
        frontendAppConfig,
        view,
        mockDataCacheConnector
      )


    override def beforeEach(): Unit = {
      reset(mockDataCacheConnector)
      reset(mockCitizensDetailsConnector)
      reset(mockSaConnector)
      reset(mockSelectSACategory)
      reset(mockSaCatController)
      super.beforeEach()
    }


  "mtdItsaSubscribedCheck" when {
    val enrolments: Enrolments = Enrolments(Set(Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(EnrolmentIdentifier("UTR", "1234567816")), state = "Activated")))
    val designatoryDetails: DesignatoryDetails = DesignatoryDetails("test", "test", "AA00000A", "test")
    val businessDetails: BusinessDetails = BusinessDetails("aa000000a", "1234567")
    implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

    "return true when citizens details and business details return a model" in {

      when(mockCitizensDetailsConnector.getDesignatoryDetails(any(),any())(any())).thenReturn(Future.successful(Some(designatoryDetails)))
      when(mockSaConnector.getBusinessDetails(any(),any())(any(),any())).thenReturn(Future.successful(Some(businessDetails)))
      when(mockDataCacheConnector.save[Boolean](any(),any(),any())(any())).thenReturn(Future.successful(emptyCacheMap))

      val result = testService.mtdItsaSubscribedCheck(Some(EnrolmentIdentifier("IR-SA", "1234567816")))
      await(result) mustBe true

    }

  }

  "getEnrolmentIdentifier" when {

    "return correct utr from SA-enrolment" in {
      val enrolments: Enrolments = Enrolments(Set(Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(EnrolmentIdentifier("UTR", "1234567816")), state = "Activated")))

      val result = testService.getEnrolmentIdentifier(enrolments)

      result.get.value mustBe "1234567816"
    }

    "return None" in {
      val enrolments: Enrolments = Enrolments(Set())

      val result = testService.getEnrolmentIdentifier(enrolments)

      result mustBe None
    }
  }



  "redirectSACategory" when {

    "IR-SA, Trusts and MTD-IT enrolments" must {

      val enrolments: Enrolments = Enrolments(Set(saEnrolment, saTrustsEnrolment, mtdITEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

      "redirect to /business-account/add-tax/self-assessment/partnership" in{
        val result = redirectSACategory

        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

      }
    }
    "IR-SA, Trusts, MTD-IT AND Sa-partnership enrolments" must {

      val enrolments: Enrolments = Enrolments(Set(saEnrolment, saTrustsEnrolment, saPartnershipsEnrolment, mtdITEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

      "redirect to /business-account/add-tax/self-assessment/partnership" in{
        val result = redirectSACategory

        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

      }
    }

    "IR-SA & MTD-IT enrolments" must {

      val enrolments: Enrolments = Enrolments(Set(saEnrolment, mtdITEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

      "should return 200" in{
        val result = redirectSACategory

        status(result) mustBe 200
      }
    }

    "MTD-IT enrolment" must {

      val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

      "should return 200" in{
        val result = redirectSACategory

        status(result) mustBe 200
      }
    }

    "IR-SA, Sa-Trust & Sa-Partnership enrolments" must {

      val enrolments: Enrolments = Enrolments(Set(saEnrolment,saTrustsEnrolment, saPartnershipsEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)
      if(mtdBoolCheck) {
        "should return 200" in {
          val result = redirectSACategory

          status(result) mustBe 200
        }
      } else {
        "redirect to /business-account/add-tax/self-assessment/partnership" in{
          val result = redirectSACategory

          redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

        }
      }
    }

    "Only Sa enrolment" must {

      val enrolments: Enrolments = Enrolments(Set(saEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

        "should return 200" in {
          val result = redirectSACategory

          status(result) mustBe 200
        }
    }

    "No enrolments" must {

      val enrolments: Enrolments = Enrolments(Set())
      val designatoryDetails: DesignatoryDetails = DesignatoryDetails("test", "test", "AA00000A", "test")
      val businessDetails: BusinessDetails = BusinessDetails("aa000000a", "1234567")


      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)





      "should return 200" in{

        when(mockDataCacheConnector.getEntry[SAUTR](any(),any())(any())).thenReturn(Future.successful(Some(SAUTR("1234567816"))))
        when(mockCitizensDetailsConnector.getDesignatoryDetails(any(),any())(any())).thenReturn(Future.successful(Some(designatoryDetails)))
        when(mockSaConnector.getBusinessDetails(any(),any())(any(),any())).thenReturn(Future.successful(Some(businessDetails)))
        when(mockDataCacheConnector.save[Boolean](any(),any(),any())(any())).thenReturn(Future.successful(emptyCacheMap))

        val redirectSACategory = testService
          .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

        val result = redirectSACategory

        status(result) mustBe 200
      }
    }

  }


  "getRadioOptions" when{

    "mtdBool is true and no enrolments" must{
      "show MTD IT, Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", MtdIT.toString),
          RadioOption("selectSACategory", Partnership.toString),
          RadioOption("selectSACategory", Trust.toString)
        )

        val enrolments: Enrolments = Enrolments(Set())

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }

    }
    "mtdBool is false and no enrolments" must{

      "show SA, Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", Sa.toString),
          RadioOption("selectSACategory", Trust.toString),
          RadioOption("selectSACategory", Partnership.toString)
        )

        val enrolments: Enrolments = Enrolments(Set())

        val result = testService.getRadioOptions(enrolments, mtdBool = false)

        result mustBe expectedRadioOptions
      }
    }
    "mtdBool is true, IR-SA enrolment" must{

      "show MTD IT, Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", MtdIT.toString),
          RadioOption("selectSACategory", Partnership.toString),
          RadioOption("selectSACategory", Trust.toString)
        )

        val enrolments: Enrolments = Enrolments(Set(saEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }
    "mtdBool is false, IR-SA enrolment" must{

      "show Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString), RadioOption("selectSACategory", Trust.toString))

        val  enrolments: Enrolments = Enrolments(Set(saEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = false)

        result mustBe expectedRadioOptions
      }
    }
    "mtdBool is true, IR-SA and Trusts enrolments" must{

      "show MTD IT, Partnerships radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", MtdIT.toString),
          RadioOption("selectSACategory", Partnership.toString)
        )

        val enrolments: Enrolments = Enrolments(Set(saEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is false, IR-SA and Trusts enrolments" must{

      "show Partnerships radio button" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString))

        val enrolments: Enrolments = Enrolments(Set(saEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = false)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, IR-SA and Partnerships enrolments" must{

      "show MTD IT, Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", MtdIT.toString),
          RadioOption("selectSACategory", Partnership.toString),
          RadioOption("selectSACategory", Trust.toString)
        )

        val enrolments: Enrolments = Enrolments(Set(saEnrolment, saPartnershipsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is false, IR-SA and Partnerships enrolments" must{

      "show Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString), RadioOption("selectSACategory", Trust.toString))

        val enrolments: Enrolments = Enrolments(Set(saEnrolment, saPartnershipsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = false)

        result mustBe expectedRadioOptions
      }
    }
    "mtdBool is true, IR-SA, Partnerships and Trusts enrolments" must {

      "show MTD IT, Partnerships radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", MtdIT.toString), RadioOption("selectSACategory", Partnership.toString))
        val enrolments: Enrolments = Enrolments(Set(saEnrolment, saPartnershipsEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is false, IR-SA, Partnerships and Trusts enrolments" must {

      "show Partnerships radio button" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString))
        val enrolments: Enrolments = Enrolments(Set(saEnrolment, saPartnershipsEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = false)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, IR-SA, Partnerships and Trusts enrolments" must{

      "show Partnerships radio button" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString))
        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saEnrolment, saPartnershipsEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, Partnerships and Trusts enrolments" must {

      "show IR-SA, Partnerships radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Sa.toString), RadioOption("selectSACategory", Partnership.toString))

        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saPartnershipsEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, IR-SA, and Trusts enrolments" must{

      "show Partnerships radio button" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString))
        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, IR-SA, Partnerships enrolments" must{

      "show Partnerships and Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString), RadioOption("selectSACategory", Trust.toString))

        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saEnrolment, saPartnershipsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, IR-SA enrolments" must{

      "show Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Partnership.toString), RadioOption("selectSACategory", Trust.toString))
        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, Partnerships enrolments" must{

      "show IR-SA, Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", Sa.toString),
          RadioOption("selectSACategory", Partnership.toString),
          RadioOption("selectSACategory", Trust.toString)
        )

        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saPartnershipsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }

    "mtdBool is true, MTD IT, Trusts enrolments" must{

      "show IR-SA, Partnerships radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(RadioOption("selectSACategory", Sa.toString), RadioOption("selectSACategory", Partnership.toString))

        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment, saTrustsEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }
    "mtdBool is true, MTD IT enrolments" must{

      "show IR-SA, Partnerships, Trusts radio buttons" in{

        val expectedRadioOptions: Set[RadioOption] = Set(
          RadioOption("selectSACategory", Sa.toString),
          RadioOption("selectSACategory", Partnership.toString),
          RadioOption("selectSACategory", Trust.toString)
        )

        val enrolments: Enrolments = Enrolments(Set(mtdITEnrolment))

        val result = testService.getRadioOptions(enrolments, mtdBool = true)

        result mustBe expectedRadioOptions
      }
    }
  }

}
