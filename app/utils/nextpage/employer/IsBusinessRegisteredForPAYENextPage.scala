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
import controllers.employer.ers.routes
import controllers.employer.intermediaries.{routes => eiRoutes}
import identifiers.IsBusinessRegisteredForPAYEId
import models.employer.IsBusinessRegisteredForPAYE
import play.api.mvc.{Call, Request}
import utils.NextPage

trait IsBusinessRegisteredForPAYENextPage {

  implicit val isBusinessRegisteredForPAYE
    : NextPage[IsBusinessRegisteredForPAYEId.CIS.type, IsBusinessRegisteredForPAYE, Call] = {
    new NextPage[IsBusinessRegisteredForPAYEId.CIS.type, IsBusinessRegisteredForPAYE, Call] {
      override def get(b: IsBusinessRegisteredForPAYE)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case IsBusinessRegisteredForPAYE.Yes =>
            payeAccountRoutes.DoesBusinessManagePAYEController.onPageLoad()
          case IsBusinessRegisteredForPAYE.No =>
            payeAccountRoutes.RegisterForPAYEController.onPageLoad()
        }
    }
  }

  implicit val eiIsBusinessRegisteredForPAYE
    : NextPage[IsBusinessRegisteredForPAYEId.EI.type, IsBusinessRegisteredForPAYE, Call] = {
    new NextPage[IsBusinessRegisteredForPAYEId.EI.type, IsBusinessRegisteredForPAYE, Call] {
      override def get(b: IsBusinessRegisteredForPAYE)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case IsBusinessRegisteredForPAYE.Yes =>
            eiRoutes.DoesBusinessManagePAYEController.onPageLoad()
          case IsBusinessRegisteredForPAYE.No =>
            eiRoutes.RegisterForPAYEController.onPageLoad()
        }
    }
  }

  implicit val ersIsBusinessRegisteredForPAYE
    : NextPage[IsBusinessRegisteredForPAYEId.ERS.type, IsBusinessRegisteredForPAYE, Call] = {
    new NextPage[IsBusinessRegisteredForPAYEId.ERS.type, IsBusinessRegisteredForPAYE, Call] {
      override def get(b: IsBusinessRegisteredForPAYE)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call =
        b match {
          case IsBusinessRegisteredForPAYE.Yes =>
            routes.DoesBusinessManagePAYEController.onPageLoad()
          case IsBusinessRegisteredForPAYE.No =>
            routes.RegisterEmployersPAYEController.onPageLoad()
        }
    }
  }

}
