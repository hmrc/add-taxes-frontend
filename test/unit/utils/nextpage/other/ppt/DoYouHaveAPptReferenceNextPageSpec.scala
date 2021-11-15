package utils.nextpage.other.ppt

import models.other.ppt.DoYouHaveAPptReference
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class DoYouHaveAPptReferenceNextPageSpec extends NextPageSpecBase {

  "DoYouHaveAPptReference" when {
    //needs to be implemented
    behave like nextPage(
      NextPage.doYouHaveAPptReference,
      DoYouHaveAPptReference.Yes,
      "http://localhost:8505/register-for-plastic-packaging-tax/enrolment-ppt-reference"
    )

    behave like nextPage(
      NextPage.doYouHaveAPptReference,
      DoYouHaveAPptReference.No,
      "/business-account/add-tax/other/ppt-no-ref"
    )
  }
}