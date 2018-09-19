/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.nextpage.sa

import config.FrontendAppConfig
import identifiers.SelectSACategoryId
import models.sa.{DoYouHaveSAUTR, SelectSACategory}
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}
import controllers.sa.{routes => saRoutes}
import controllers.sa.partnership.{routes => saPartnerRoutes}
import controllers.sa.trust.{routes => trustRoutes}
import uk.gov.hmrc.auth.core.AffinityGroup

trait SelectSACategoryNextPage {

  type SelectSACategoryWithAffinityGroup = (SelectSACategory, DoYouHaveSAUTR, Option[AffinityGroup])

  implicit val selectSACategory: NextPage[SelectSACategoryId.type, SelectSACategoryWithAffinityGroup, Call] = {

    new NextPage[SelectSACategoryId.type, SelectSACategoryWithAffinityGroup, Call] {
      override def get(saCategory: SelectSACategoryWithAffinityGroup)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        (saCategory._1, saCategory._2, saCategory._3) match {

          case (SelectSACategory.Sa, DoYouHaveSAUTR.Yes, _) => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.SA))

          case (SelectSACategory.Sa, _, Some(AffinityGroup.Individual)) =>
            saRoutes.AreYouSelfEmployedController.onPageLoad()

          case (SelectSACategory.Sa, _, _) => Call("GET", appConfig.getPortalUrl("selectTaxes"))

          case (SelectSACategory.Partnership, _, Some(AffinityGroup.Organisation)) =>
            saPartnerRoutes.DoYouWantToAddPartnerController.onPageLoad()

          case (SelectSACategory.Partnership, _, _) => saPartnerRoutes.SetUpNewAccountController.onPageLoad()

          case (SelectSACategory.Trust, _, Some(AffinityGroup.Organisation)) =>
            trustRoutes.HaveYouRegisteredTrustController.onPageLoad()

          case (SelectSACategory.Trust, _, _) => trustRoutes.SetUpNewAccountController.onPageLoad()
        }
    }
  }
}
