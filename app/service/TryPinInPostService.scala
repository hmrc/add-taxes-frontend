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

package service

import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.Assets.{InternalServerError, Redirect}
import handlers.ErrorHandler
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.SAUTR
import play.api.Logging
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class TryPinInPostService @Inject()(dataCacheConnector: DataCacheConnector,
                                   enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                   errorHandler: ErrorHandler
                                   ) extends Logging {

  def checkEnrol()(implicit request: ServiceInfoRequest[AnyContent],
                   ec: ExecutionContext,
                   hc: HeaderCarrier): Future[Result] = {
    val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
    val enrolForSaBoolean: Future[Boolean] = utr.flatMap {
      maybeSAUTR =>
        (
          for {
            utr <- maybeSAUTR
          } yield enrolmentStoreProxyConnector.enrolForSa(utr.value, request.request.credId, request.request.groupId, "enrolOnly")
          ).getOrElse(Future.successful(false))
    }

    enrolForSaBoolean.map {
      case true  => Redirect(controllers.sa.routes.RequestedAccessController.onPageLoad())
      case false => InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }

}
