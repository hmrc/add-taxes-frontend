/*
 * Copyright 2021 HM Revenue & Customs
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
import identifiers.DoYouNeedToStopMGDId
import play.api.mvc.{Call, Request}
import models.deenrolment.DoYouNeedToStopMGD
import playconfig.featuretoggle.FeatureConfig
import uk.gov.hmrc.auth.core.Enrolment
import utils.{Enrolments, NextPage}

trait DoYouNeedToStopMGDNextPage {

  type DoYouNeedToStopMGDWithEnrolment = (DoYouNeedToStopMGD, Option[Enrolment])

  implicit val doYouNeedToStopMGD
    : NextPage[DoYouNeedToStopMGDId.type, DoYouNeedToStopMGDWithEnrolment, Either[String, Call]] = {
    new NextPage[DoYouNeedToStopMGDId.type, DoYouNeedToStopMGDWithEnrolment, Either[String, Call]] {
      override def get(b: DoYouNeedToStopMGDWithEnrolment)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Either[String, Call] =
        b match {

          case (DoYouNeedToStopMGD.Yes, _) =>
            Right(Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.MachineGamesDuty)))

          case (DoYouNeedToStopMGD.No, Some(enrolment)) =>
            enrolment.identifiers.toList match {
              case Nil    => Left(s"unable to find identifier for ${enrolment.key}")
              case h :: _ => Right(Call("GET", appConfig.getPortalUrl("stopMGD", h.value)))
            }
          case (DoYouNeedToStopMGD.No, None) => Left("unable to find enrolment")

        }
    }
  }
}
