package utils.nextpage.employer.cis

import models.employer.cis.IsYourBusinessInUK
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class IsYourBusinessInUKNextPageSpec extends NextPageSpecBase {

  "isYourBusinessInUK" when {
    behave like nextPage(
      NextPage.isYourBusinessInUK,
      IsYourBusinessInUK.Yes,
      "#"
    )

    behave like nextPage(
      NextPage.isYourBusinessInUK,
      IsYourBusinessInUK.No,
      "#"
    )
  }
}