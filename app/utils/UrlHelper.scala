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

import javax.inject.{Inject, Singleton}

import config.FrontendAppConfig

@Singleton
class UrlHelper @Inject()(val appConfig: FrontendAppConfig) {
  private val emacHost = appConfig.enrolmentManagementFrontendHost
  private val lostCredentialsHost = appConfig.governmentGatewayLostCredentialsFrontendHost


  def registerForTaxUrl(enrolment: Enrolments): String = {
    s"$emacHost/enrolment-management-frontend/${enrolment.toString}/request-access-tax-scheme?continue=%2Fbusiness-account"
  }

  def governmentGatewayLostCredentialsUrl(forgottenOption: ForgottenOptions): String = {
    s"$lostCredentialsHost/government-gateway-lost-credentials-frontend/" +
      s"choose-your-account?continue=%2Fbusiness-account&origin=business-tax-account&forgottenOption=$forgottenOption"
  }

}
