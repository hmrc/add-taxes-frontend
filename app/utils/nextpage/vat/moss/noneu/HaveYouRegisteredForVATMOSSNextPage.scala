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

package utils.nextpage.vat.moss.noneu

import config.FrontendAppConfig
import identifiers.HaveYouRegisteredForVATMOSSId
import play.api.mvc.{Call, Request}
import models.vat.moss.noneu.HaveYouRegisteredForVATMOSS
import playconfig.featuretoggle.FeatureConfig
import utils.{Enrolments, NextPage}

trait HaveYouRegisteredForVATMOSSNextPage {

  implicit val haveYouRegisteredForVATMOSS
    : NextPage[HaveYouRegisteredForVATMOSSId.type, HaveYouRegisteredForVATMOSS, Call] = {
    new NextPage[HaveYouRegisteredForVATMOSSId.type, HaveYouRegisteredForVATMOSS, Call] {
      override def get(b: HaveYouRegisteredForVATMOSS)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case HaveYouRegisteredForVATMOSS.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.VATMOSSNonUnion))
          case HaveYouRegisteredForVATMOSS.No  => Call("GET", appConfig.getPortalUrl("mossRegistration"))
        }
    }
  }
}
