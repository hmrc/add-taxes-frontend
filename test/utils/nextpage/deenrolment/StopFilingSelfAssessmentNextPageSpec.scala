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

package utils.nextpage.deenrolment

import models.deenrolment.StopFilingSelfAssessment
import utils.NextPage
import utils.nextpage.NextPageSpecBase

class StopFilingSelfAssessmentNextPageSpec extends NextPageSpecBase {

  "stopFilingSelfAssessment" when {
    behave like nextPage(
      NextPage.stopFilingSelfAssessment,
      StopFilingSelfAssessment.Yes,
      "http://localhost:9020/business-account/self-assessment/stop"
    )

    behave like nextPage(
      NextPage.stopFilingSelfAssessment,
      StopFilingSelfAssessment.No,
      "http://localhost:9555/enrolment-management-frontend/IR-SA/remove-access-tax-scheme?continue=%2Fbusiness-account"
    )
  }
}
