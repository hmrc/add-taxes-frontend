/*
 * Copyright 2019 HM Revenue & Customs
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

package utils.nextpage.employer.cis

import config.FrontendAppConfig
import identifiers.IsYourBusinessInUKId
import play.api.mvc.{Call, Request}
import models.employer.cis.IsYourBusinessInUK
import utils.{Enrolments, NextPage}
import controllers.employer.cis.ukbased.routes._
import playconfig.featuretoggle.FeatureConfig

trait IsYourBusinessInUKNextPage {

  implicit val isYourBusinessInUK: NextPage[IsYourBusinessInUKId.type, IsYourBusinessInUK, Call] = {

    new NextPage[IsYourBusinessInUKId.type, IsYourBusinessInUK, Call] {

      override def get(b: IsYourBusinessInUK)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {

          case IsYourBusinessInUK.Yes => AreYouContractorOrSubcontractorController.onPageLoad()

          case IsYourBusinessInUK.No => Call("GET", appConfig.getGovUKUrl("cisOutsideUk"))
        }
    }
  }
}
