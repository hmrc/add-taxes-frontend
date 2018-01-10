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

package utils

import controllers.routes
import identifiers.{HaveYouRegisteredForTiedOilsId, SelectAnOilServiceId}
import models.HaveYouRegisteredForTiedOils.{No, Yes}
import models.{HaveYouRegisteredForTiedOils, NormalMode, SelectAnOilService}
import models.SelectAnOilService.{RebatedOilsEnquiryService, TiedOilsEnquiryService}
import play.api.mvc.Call

trait NextPage[A, B] {
  def get(b: B): Call
}

object NextPage {

  implicit val selectAnOilService: NextPage[SelectAnOilServiceId.type, SelectAnOilService] =
    new NextPage[SelectAnOilServiceId.type, SelectAnOilService] {
      override def get(b: SelectAnOilService): Call =
        b match {
          case RebatedOilsEnquiryService => routes.IndexController.onPageLoad()
          case TiedOilsEnquiryService => routes.HaveYouRegisteredForTiedOilsController.onPageLoad(NormalMode)
        }
    }

  implicit val haveYouRegisteredForTiedOils: NextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils] = {
    new NextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils] {
      override def get(b: HaveYouRegisteredForTiedOils): Call =
        b match {
          case Yes => routes.IndexController.onPageLoad()
          case No => routes.IndexController.onPageLoad()
        }
    }
  }
}