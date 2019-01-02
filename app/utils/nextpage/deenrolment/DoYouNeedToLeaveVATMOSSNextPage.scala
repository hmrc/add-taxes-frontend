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

package utils.nextpage.deenrolment

import config.FrontendAppConfig
import identifiers.DoYouNeedToLeaveVATMOSSId
import models.deenrolment.DoYouNeedToLeaveVATMOSS
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.auth.core.Enrolment
import utils.{Enrolments, NextPage}

trait DoYouNeedToLeaveVATMOSSNextPage {

  type DoYouNeedToLeaveVATMOSSWithEnrolment = (DoYouNeedToLeaveVATMOSS, Option[Enrolment])

  implicit val doYouNeedToLeaveVATMOSS
    : NextPage[DoYouNeedToLeaveVATMOSSId.type, DoYouNeedToLeaveVATMOSSWithEnrolment, Either[String, Call]] = {
    new NextPage[DoYouNeedToLeaveVATMOSSId.type, DoYouNeedToLeaveVATMOSSWithEnrolment, Either[String, Call]] {
      override def get(b: DoYouNeedToLeaveVATMOSSWithEnrolment)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Either[String, Call] =
        b match {
          case (DoYouNeedToLeaveVATMOSS.Yes, Some(enrolment)) =>
            enrolment.identifiers match {
              case Nil    => Left(s"unable to find identifier for ${enrolment.key}")
              case h :: _ => Right(Call("GET", appConfig.getPortalUrl("mossChangeDetails", h.value)))
            }
          case (DoYouNeedToLeaveVATMOSS.Yes, None) => Left("unable to find enrolment")
          case (DoYouNeedToLeaveVATMOSS.No, _)     => Right(Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.VATMOSS)))
        }
    }
  }
}
