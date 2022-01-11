/*
 * Copyright 2022 HM Revenue & Customs
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

package utils.nextpage.employer

import config.FrontendAppConfig
import controllers.employer.cis.ukbased.contractor.{routes => payeAccountRoutes}
import controllers.employer.ers.{routes => ersRoutes}
import controllers.employer.intermediaries.{routes => eiAccountRoutes}
import identifiers.DoesBusinessManagePAYEId
import models.employer.DoesBusinessManagePAYE
import play.api.mvc.{Call, Request}
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}

trait DoesBusinessManagePAYENextPage {

  implicit val epayeDoesBusinessManagePAYE
    : NextPage[DoesBusinessManagePAYEId.EPaye.type, DoesBusinessManagePAYE, Call] = {
    new NextPage[DoesBusinessManagePAYEId.EPaye.type, DoesBusinessManagePAYE, Call] {
      override def get(b: DoesBusinessManagePAYE)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoesBusinessManagePAYE.Yes => payeAccountRoutes.UsePAYEEmployerAccountController.onPageLoad()
          case DoesBusinessManagePAYE.No  => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.AddCis))
        }
    }
  }

  implicit val eiDoesBusinessManagePAYE: NextPage[DoesBusinessManagePAYEId.EI.type, DoesBusinessManagePAYE, Call] = {
    new NextPage[DoesBusinessManagePAYEId.EI.type, DoesBusinessManagePAYE, Call] {
      override def get(b: DoesBusinessManagePAYE)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoesBusinessManagePAYE.Yes => eiAccountRoutes.UsePAYEEmployerAccountController.onPageLoad()
          case DoesBusinessManagePAYE.No  => Call("GET", appConfig.eiUrl)
        }
    }
  }

  implicit val ersDoesBusinessManagePAYE: NextPage[DoesBusinessManagePAYEId.ERS.type, DoesBusinessManagePAYE, Call] = {
    new NextPage[DoesBusinessManagePAYEId.ERS.type, DoesBusinessManagePAYE, Call] {
      override def get(b: DoesBusinessManagePAYE)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case DoesBusinessManagePAYE.Yes => ersRoutes.UseEmployersPAYEController.onPageLoad()
          case DoesBusinessManagePAYE.No  => ersRoutes.AddEmployersPAYEController.onPageLoad()
        }
    }
  }
}
