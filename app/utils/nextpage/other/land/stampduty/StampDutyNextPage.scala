/*
 * Copyright 2023 HM Revenue & Customs
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

package utils.nextpage.other.land.stampduty

import config.FrontendAppConfig
import identifiers.StampDutyId
import play.api.mvc.{Call, Request}
import models.other.land.stampduty.StampDuty
import utils.NextPage
import controllers.other.land.stampduty.{routes => stampDutyRoutes}

trait StampDutyNextPage {

  implicit val stampDuty: NextPage[StampDutyId.type, StampDuty, Call] = {
    new NextPage[StampDutyId.type, StampDuty, Call] {
      override def get(
        b: StampDuty)(implicit appConfig: FrontendAppConfig,  request: Request[_]): Call =
        b match {
          case StampDuty.Yes => Call("GET", appConfig.getStampDutyUrl("enrol"))
          case StampDuty.No  => stampDutyRoutes.PaperFormsController.onPageLoad()
        }
    }
  }
}
