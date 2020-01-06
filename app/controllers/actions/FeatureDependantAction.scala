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

package controllers.actions

import javax.inject.Inject
import play.api.mvc.{ActionFilter, Result}
import playconfig.featuretoggle.{Feature, FeatureConfig}
import uk.gov.hmrc.http.NotFoundException

import scala.concurrent.Future
import scala.language.higherKinds

class FeatureDependantAction @Inject()(config: FeatureConfig) {

  def permitFor[R[_]](feature: Feature): ActionFilter[R] = new ActionFilter[R] {
    override protected def filter[A](request: R[A]): Future[Option[Result]] = config.isEnabled(feature) match {
      case false => Future.failed(new NotFoundException("The page is not enabled"))
      case true  => Future.successful(None)
    }
  }

}
