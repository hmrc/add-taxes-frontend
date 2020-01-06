/*
 * Copyright 2020 HM Revenue & Customs
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
import identifiers.DoYouNeedToStopVatMossNUId
import play.api.mvc.{Call, Request}
import models.deenrolment.DoYouNeedToStopVatMossNU
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}
import uk.gov.hmrc.auth.core.Enrolment

trait DoYouNeedToStopVatMossNUNextPage {

  implicit val doYouNeedToStopVatMossNU
    : NextPage[DoYouNeedToStopVatMossNUId.type, (DoYouNeedToStopVatMossNU, Option[Enrolment]), Either[String, Call]] = {
    new NextPage[DoYouNeedToStopVatMossNUId.type, (DoYouNeedToStopVatMossNU, Option[Enrolment]), Either[String, Call]] {
      override def get(b: (DoYouNeedToStopVatMossNU, Option[Enrolment]))(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Either[String, Call] =
        b match {
          case (DoYouNeedToStopVatMossNU.Yes, Some(enrolment)) =>
            enrolment.identifiers match {
              case Nil    => Left(s"unable to find identifier for ${enrolment.key}")
              case h :: _ => Right(Call("GET", appConfig.getPortalUrl("mossChangeDetails", h.value)))
            }
          case (DoYouNeedToStopVatMossNU.Yes, None) => Left("unable to find enrolment")
          case (DoYouNeedToStopVatMossNU.No, _) =>
            Right(Call("GET", appConfig.emacDeenrolmentsUrl(Enrolments.VATMOSSNonUnion)))
        }
    }
  }
}
