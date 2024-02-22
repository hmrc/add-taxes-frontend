/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.actions

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.FeatureSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import javax.inject.Inject
import play.api.mvc.{ActionFilter, Result}
import uk.gov.hmrc.http.NotFoundException

import scala.concurrent.{ExecutionContext, Future}

class FeatureDependantAction @Inject()(implicit val appConfig: FrontendAppConfig,
                                       ec: ExecutionContext) {

  def permitFor[R[_]](feature: FeatureSwitch): ActionFilter[R] = new ActionFilter[R] {
    val executionContext: ExecutionContext = ec

    override protected def filter[A](request: R[A]): Future[Option[Result]] =
      if (isEnabled(feature)) {
        Future.successful(None)
      } else {
        Future.failed(new NotFoundException("The page is not enabled"))
      }
  }

}
