/*
 * Copyright 2022 HM Revenue & Customs
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

import config.FrontendAppConfig
import connectors.{DataCacheConnector, SaConnector}
import controllers.sa.{routes => saRoutes}
import javax.inject.Inject
import models.requests.ServiceInfoRequest
import models.sa.{IvLinks, YourSaIsNotInThisAccount}
import utils.LoggingUtil
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SaService @Inject()(saConnector: SaConnector,
                          dataCacheConnector: DataCacheConnector,
                          appConfig: FrontendAppConfig) extends LoggingUtil {

  val serviceUrl: String = appConfig.identityVerificationHost

  def getIvRedirectLink(utr: String, origin: String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext, request: ServiceInfoRequest[AnyContent]): Future[String] = {

    saConnector.getIvLinks(utr, origin).map {
      case Some(ivLinks) =>
        dataCacheConnector.save[IvLinks](request.request.credId, "IvLinksId", ivLinks)
        s"${serviceUrl}${ivLinks.link}"
      case _ =>
        errorLog("[SaService][getIvRedirectLink] Failed retrieving IV link from SA")
        saRoutes.TryPinInPostController.onPageLoad(status = Some("MatchingError"), origin).url
    }
  }

  def yourSaIsNotInThisAccount(yourSaIsNotInThisAccount: YourSaIsNotInThisAccount, origin: String): Future[Result] = {
    yourSaIsNotInThisAccount match {
      case YourSaIsNotInThisAccount.LookInOtherAccount =>
        Future.successful(Redirect(Call("GET", appConfig.getBusinessAccountUrl("wrong-credentials"))))
      case YourSaIsNotInThisAccount.AddToThisAccount   =>
        Future.successful(Redirect(saRoutes.SelectSACategoryController.onPageLoadHasUTR(Some(origin))))
    }
  }

}
