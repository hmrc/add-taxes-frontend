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

package utils.nextpage.employer.pension

import config.FrontendAppConfig
import identifiers.DoYouHavePractitionerIDId
import play.api.mvc.{Call, Request}
import models.employer.pension.DoYouHavePractitionerID
import utils.NextPage
import controllers.employer.pension.routes._

trait DoYouHavePractitionerIDNextPage {

  implicit val doYouHavePractitionerID: NextPage[DoYouHavePractitionerIDId.type, DoYouHavePractitionerID, Call] = {
    new NextPage[DoYouHavePractitionerIDId.type, DoYouHavePractitionerID, Call] {
      override def get(b: DoYouHavePractitionerID)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case DoYouHavePractitionerID.Yes => DoYouHaveActivationTokenController.onPageLoad()
          case DoYouHavePractitionerID.No  => ApplyForPractitionerIDController.onPageLoad()
        }
    }
  }
}
