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

import javax.inject.Inject
import connectors.DataCacheConnector
import models.requests.{AuthenticatedRequest, OptionalDataRequest}
import play.api.mvc.ActionTransformer
import utils.UserAnswers

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject()(val dataCacheConnector: DataCacheConnector,
                                        implicit val executionContext: ExecutionContext) extends DataRetrievalAction {

  override protected def transform[A](request: AuthenticatedRequest[A]): Future[OptionalDataRequest[A]] = {

    dataCacheConnector.fetch(request.externalId).map {
      case None       => OptionalDataRequest(request.request, request.externalId, None)
      case Some(data) => OptionalDataRequest(request.request, request.externalId, Some(new UserAnswers(data)))
    }
  }
}

trait DataRetrievalAction extends ActionTransformer[AuthenticatedRequest, OptionalDataRequest]