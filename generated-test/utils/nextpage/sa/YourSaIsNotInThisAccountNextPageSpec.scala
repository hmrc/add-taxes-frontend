package utils.nextpage.sa

import models.sa.YourSaIsNotInThisAccount
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class YourSaIsNotInThisAccountNextPageSpec extends NextPageSpecBase {

  "yourSaIsNotInThisAccount" when {
    behave like nextPage(
      NextPage.yourSaIsNotInThisAccount,
      YourSaIsNotInThisAccount.LookInOtherAccount,
      "#"
    )

    behave like nextPage(
      NextPage.yourSaIsNotInThisAccount,
      YourSaIsNotInThisAccount.AddToThisAccount,
      "#"
    )
  }
}