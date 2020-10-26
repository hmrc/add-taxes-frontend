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
import controllers.Assets.Redirect
import controllers.sa.{EnrolmentSuccessController, routes => saRoutes}
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{KnownFacts, KnownFactsReturn, SAUTR}
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsService @Inject()(enrolForSaService: EnrolForSaService,
                                  saService: SaService,
                                  dataCacheConnector: DataCacheConnector,
                                  enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                  enrolmentSuccessController: EnrolmentSuccessController){

  def knownFactsLocation(knownFacts: KnownFacts)
                        (implicit request: ServiceInfoRequest[AnyContent],
                         ec: ExecutionContext,
                         hc: HeaderCarrier): Future[Result] = {
    val utr = dataCacheConnector.getEntry[SAUTR](request.request.externalId, EnterSAUTRId.toString)
    val queryKnownFactsResult: Future[KnownFactsReturn] = utr.flatMap {
      maybeSAUTR => (
          for {
            utr <- maybeSAUTR
          } yield enrolmentStoreProxyConnector.queryKnownFacts(utr, knownFacts)
        ).getOrElse(Future.successful(KnownFactsReturn("", knownFactsResult = false)))
      }

    queryKnownFactsResult.flatMap {
      case result@KnownFactsReturn(_, true) if(request.request.confidenceLevel.level >= 200) =>
        enrolForSaService.enrolForSa(result.utr, request.request.credId, request.request.groupId) map {
          case true => Redirect(saRoutes.EnrolmentSuccessController.onPageLoad())
          case _    => Redirect(saRoutes.TryPinInPostController.onPageLoad())
        }
      case result@KnownFactsReturn(_, true) =>
        saService.getIvRedirectLink(result.utr).map(link => Redirect(Call("GET", link)))
      case _ => Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad()))
    }
  }

}
