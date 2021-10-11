package service

import connectors.{CitizensDetailsConnector, DataCacheConnector, GetBusinessDetailsConnector}
import controllers.ControllerSpecBase
import controllers.sa.SelectSACategoryController
import forms.sa.SelectSACategoryFormProvider
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import models.sa.SelectSACategory
import models.sa.SelectSACategory.{MtdIT, Partnership, Sa, Trust}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.mvc.{AnyContent, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{HmrcEnrolmentType, RadioOption}
import views.html.sa.selectSACategory
import scala.concurrent.ExecutionContext

class CredFinderServiceSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach{

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val saEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(), state = "Activated")

    val saPartnershipsEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.Partnerships.toString, identifiers = Seq(), state = "Activated")

    val saTrustsEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.RegisterTrusts.toString, identifiers = Seq(), state = "Activated")

    val mtdITEnrolment: Enrolment = Enrolment(key = HmrcEnrolmentType.MTDIT.toString, identifiers = Seq(), state = "Activated")

    def test(affinityGroup: AffinityGroup, enrolments: Enrolments): ServiceInfoRequest[AnyContent] = ServiceInfoRequest[AnyContent](
      AuthenticatedRequest(FakeRequest(), "", enrolments, Some(affinityGroup), groupId, providerId, confidenceLevel, None),
      HtmlFormat.empty)

    val mockDataCacheConnector: DataCacheConnector = mock[DataCacheConnector]
    val mockCitizensDetailsConnector: CitizensDetailsConnector = mock[CitizensDetailsConnector]
    val mockGetBusinessDetailsConnector: GetBusinessDetailsConnector = mock[GetBusinessDetailsConnector]
    val mockSelectSACategory: selectSACategory = mock[selectSACategory]
    val mockCall: Call = controllers.sa.routes.SelectSACategoryController.onPageLoadHasUTR(Some("bta"))
    val mockSaCatController: SelectSACategoryController = mock[SelectSACategoryController]

  val formProvider = new SelectSACategoryFormProvider()
    val form: Form[SelectSACategory] = formProvider()

    val btaOrigin: String = "bta-sa"

    def testService: CredFinderService = new CredFinderService(
        mockCitizensDetailsConnector,
        mockGetBusinessDetailsConnector,
        frontendAppConfig,
        mockSelectSACategory,
        mockDataCacheConnector
      )


    override def beforeEach(): Unit = {
      super.beforeEach()
    }

  "redirectSACategory" when {

    "IR-SA, Trusts and MTD-IT enrolments" must {

      val  enrolments: Enrolments = Enrolments(Set(saEnrolment, saTrustsEnrolment, mtdITEnrolment))

      implicit val request: ServiceInfoRequest[AnyContent] = test(Organisation, enrolments)

      val redirectSACategory = testService
        .redirectSACategory(form, mockCall, btaOrigin )(hc, messages = messages, request = request, ec = ExecutionContext.Implicits.global)

      "redirect to /business-account/add-tax/self-assessment/partnership" in{
        val result = redirectSACategory

        redirectLocation(result) mustBe Some("/business-account/add-tax/self-assessment/partnership")

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
