package utils.nextpage.$package$

import models.$package$.$className$
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class $className$NextPageSpec extends NextPageSpecBase {

  "$className;format="decap"$" when {
    behave like nextPage(
      NextPage.$className;format="decap"$,
      $className$.Option1,
      "#"
    )

    behave like nextPage(
      NextPage.$className;format="decap"$,
      $className$.Option2,
      "#"
    )
  }
}