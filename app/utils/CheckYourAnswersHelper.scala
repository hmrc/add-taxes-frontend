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
import models.CheckMode
import viewmodels.{AnswerRow, RepeaterAnswerRow, RepeaterAnswerSection}

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def haveYouRegisteredForRebatedOils: Option[AnswerRow] = userAnswers.haveYouRegisteredForRebatedOils map {
    x => AnswerRow("haveYouRegisteredForRebatedOils.checkYourAnswersLabel", s"haveYouRegisteredForRebatedOils.$x", true, routes.HaveYouRegisteredForRebatedOilsController.onPageLoad(CheckMode).url)
  }

  def haveYouRegisteredForTiedOils: Option[AnswerRow] = userAnswers.haveYouRegisteredForTiedOils map {
    x => AnswerRow("haveYouRegisteredForTiedOils.checkYourAnswersLabel", s"haveYouRegisteredForTiedOils.$x", true, routes.HaveYouRegisteredForTiedOilsController.onPageLoad(CheckMode).url)
  }

  def selectAnOilService: Option[AnswerRow] = userAnswers.selectAnOilService map {
    x => AnswerRow("selectAnOilService.checkYourAnswersLabel", s"selectAnOilService.$x", true, routes.SelectAnOilServiceController.onPageLoad(CheckMode).url)
  }
}
