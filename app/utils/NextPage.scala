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
import identifiers.{HaveYouRegisteredForRebatedOilsId, HaveYouRegisteredForTiedOilsId, SelectAnOilServiceId}
import models.SelectAnOilService.{RebatedOilsEnquiryService, TiedOilsEnquiryService}
import models.{HaveYouRegisteredForRebatedOils, HaveYouRegisteredForTiedOils, NormalMode, SelectAnOilService}
import play.api.mvc.Call

trait NextPage[A, B] {
  def get(b: B): Call
}

object NextPage {

  implicit val selectAnOilService: NextPage[SelectAnOilServiceId.type, SelectAnOilService] =
    new NextPage[SelectAnOilServiceId.type, SelectAnOilService] {
      override def get(b: SelectAnOilService): Call =
        b match {
          case RebatedOilsEnquiryService => routes.HaveYouRegisteredForRebatedOilsController.onPageLoad(NormalMode)
          case TiedOilsEnquiryService => routes.HaveYouRegisteredForTiedOilsController.onPageLoad(NormalMode)
        }
    }

  implicit val haveYouRegisteredForTiedOils: NextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils] = {
    new NextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils] {
      override def get(b: HaveYouRegisteredForTiedOils): Call =
        b match {
          case models.HaveYouRegisteredForTiedOils.Yes => Call("GET", "http://localhost:9555/enrolment-management-frontend/HMCE-TO/request-access-tax-scheme?continue=%2Fbusiness-account")
          case models.HaveYouRegisteredForTiedOils.No => routes.RegisterTiedOilsController.onPageLoad()
        }
    }
  }

  implicit val haveYouRegisteredForRebatedOils: NextPage[HaveYouRegisteredForRebatedOilsId.type, HaveYouRegisteredForRebatedOils] = {
    new NextPage[HaveYouRegisteredForRebatedOilsId.type, HaveYouRegisteredForRebatedOils] {
      override def get(b: HaveYouRegisteredForRebatedOils): Call =
        b match {
          case models.HaveYouRegisteredForRebatedOils.Yes => Call("GET", "http://localhost:9555/enrolment-management-frontend/HMCE-RO/request-access-tax-scheme?continue=%2Fbusiness-account")
          case models.HaveYouRegisteredForRebatedOils.No => routes.RegisterRebatedOilsController.onPageLoad()
        }
    }
  }
}