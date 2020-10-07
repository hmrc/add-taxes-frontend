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

import connectors.{DataCacheConnector, IvConnector}
import controllers.Assets.Redirect
import controllers.sa.{routes => saRoutes}
import identifiers.EnterSAUTRId
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{IvLinks, SAUTR}
import play.api.Logger
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class IvService @Inject()(dataCacheConnector: DataCacheConnector,
                         ivConnector: IvConnector,
                         enrolForSaService: EnrolForSaService) {

  def journeyLinkCheck()(implicit request: ServiceInfoRequest[AnyContent],
                         ec: ExecutionContext,
                         hc: HeaderCarrier): Future[Boolean] = {

    val journeyLink: Future[Option[IvLinks]] = dataCacheConnector.getEntry[IvLinks](request.request.externalId, "IvLinksId")

    journeyLink.flatMap {
      case Some(x) => ivConnector.checkJourneyLink(x.journeyLink).map {
            pass => pass.result.equals("Success")
      }.recover {
        case exception =>
          Logger.error("Enrolment Store Proxy error", exception)
          false
      }
      case _ => Future.successful(false)
    }
  }

  def ivCheckAndEnrol()(implicit request: ServiceInfoRequest[AnyContent],
                        ec: ExecutionContext,
                        hc: HeaderCarrier): Future[Result] = {
    journeyLinkCheck().flatMap { result =>
      if(result) {
        val utr = dataCacheConnector.getEntry[SAUTR](request.request.externalId, EnterSAUTRId.toString)
        val enrolForSaBoolean: Future[Boolean] = utr.flatMap {
          maybeSAUTR =>
            (
              for {
                utr <- maybeSAUTR
              } yield enrolForSaService.enrolForSa(utr.value, request.request.credId, request.request.groupId)
              ).getOrElse(Future.successful(false))
        }
        enrolForSaBoolean.map {
          case true => Redirect(saRoutes.EnrolmentSuccessController.onPageLoad())
          case _    => Redirect(saRoutes.TryPinInPostController.onPageLoad())
        }
      } else Future.successful(Redirect(saRoutes.TryPinInPostController.onPageLoad()))
    }
  }

}
