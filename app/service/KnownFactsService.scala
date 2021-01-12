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

package service

import connectors.{DataCacheConnector, EnrolmentStoreProxyConnector}
import controllers.Assets.Redirect
import controllers.sa.{EnrolmentSuccessController, routes => saRoutes}
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{CredIdFound, DoYouHaveSAUTR, EnrolmentCheckResult, GroupIdFound, KnownFacts, KnownFactsReturn, NoRecordFound, NoSaUtr, SAUTR, SelectSACategory}
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.{ExecutionContext, Future}

class KnownFactsService @Inject()(saService: SaService,
                                  dataCacheConnector: DataCacheConnector,
                                  enrolmentStoreProxyConnector: EnrolmentStoreProxyConnector,
                                  auditService: AuditService){

  def knownFactsLocation(knownFacts: KnownFacts)
                        (implicit request: ServiceInfoRequest[AnyContent],
                         ec: ExecutionContext,
                         hc: HeaderCarrier): Future[Result] = {
    val utr = dataCacheConnector.getEntry[SAUTR](request.request.credId, EnterSAUTRId.toString)
    val queryKnownFactsResult: Future[KnownFactsReturn] = utr.flatMap {
      maybeSAUTR => (
          for {
            utr <- maybeSAUTR
          } yield enrolmentStoreProxyConnector.queryKnownFacts(utr, knownFacts)
        ).getOrElse(Future.successful(KnownFactsReturn("", knownFactsResult = false)))
      }

    queryKnownFactsResult.flatMap {
      case result@KnownFactsReturn(_, true) =>
        saService.getIvRedirectLink(result.utr).map(link => Redirect(Call("GET", link)))
      case _ => Future.successful(Redirect(saRoutes.RetryKnownFactsController.onPageLoad()))
    }
  }

  def enrolmentCheck(credId: String,
                     saUTR: SAUTR,
                     groupId: String,
                     saEnrolment: String,
                     doYouHaveSaUtr: DoYouHaveSAUTR)(implicit request: ServiceInfoRequest[AnyContent],
                                                   ec: ExecutionContext,
                                                   hc: HeaderCarrier): Future[EnrolmentCheckResult] = {

   val enrolmentCheck: Future[EnrolmentCheckResult] = {
     if(doYouHaveSaUtr.equals(DoYouHaveSAUTR.Yes)) {
       enrolmentStoreProxyConnector.checkExistingUTR(saUTR.value, saEnrolment).flatMap { enrolmentStoreResult =>
         if (!enrolmentStoreResult) {
           enrolmentStoreProxyConnector.checkSaGroup(groupId, saEnrolment).map(
             res => if (res) GroupIdFound else NoRecordFound
           )
         } else { Future.successful(CredIdFound) }
       }
     } else { Future.successful(NoSaUtr) }
   }

    enrolmentCheck.map { enrolmentCheckResult =>
      auditService.auditSA(credId, saUTR.value, enrolmentCheckResult)
    }

    enrolmentCheck
  }
}
