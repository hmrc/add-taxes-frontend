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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import models.sa._
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreProxyConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClient) extends Logging {

  def checkExistingUTR(utr: String, saEnrolment: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET[HttpResponse](appConfig.checkUtrUrl(utr, saEnrolment)).map { response =>
      response.status match {
        case OK => response.json.as[ExistingUtrModel].principalUserIds.nonEmpty
        case NO_CONTENT => false
        case other =>
          logger.error(s"[EnrolmentStoreProxyConnector][checkExistingUTR] Enrolment Store Proxy error with status $other")
          false
      }
    } recover {
      case exception =>
        logger.error("[EnrolmentStoreProxyConnector][checkExistingUTR] Enrolment Store Proxy error", exception)
        false
    }

  def checkSaGroup(groupId: String, saEnrolment: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET[HttpResponse](appConfig.checkSaGroupUrl(groupId, saEnrolment)).map {
      response =>
        response.status match {
          case OK => response.json.as[QueryGroupsEnrolmentsResponseModel].enrolments.exists(_.service.contains(saEnrolment))
          case NO_CONTENT => false
          case other =>
            logger.error(s"[EnrolmentStoreProxyConnector][checkSaGroup] Enrolment Store Proxy error with status $other")
            false
        }
    } recover {
      case exception =>
        logger.error("[EnrolmentStoreProxyConnector][checkSaGroup] Enrolment Store Proxy error", exception)
        false
    }


  def checkExistingEmpRef(officeNumber: String, payeReference: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.GET[HttpResponse](appConfig.checkEmpRefUrl(officeNumber, payeReference)).map { response =>
      response.status match {
        case OK         => true
        case NO_CONTENT => false
        case status =>
          logger.error(s"[EnrolmentStoreProxyConnector][checkExistingEmpRef] Enrolment Store Proxy returned status code: $status, body: ${response.body}")
          false
      }
    } recover {
      case exception =>
        logger.error("[EnrolmentStoreProxyConnector][checkExistingEmpRef] Enrolment Store Proxy error", exception)
        false
    }

  def queryKnownFacts(utr: SAUTR, knownFacts: KnownFacts)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[KnownFactsReturn] = {
    val knownFactsCombined = KnownFactsAndIdentifiers(utr.value, knownFacts.nino, knownFacts.postcode, knownFacts.isAbroad)

    http.POST[KnownFactsAndIdentifiers, HttpResponse](appConfig.queryKnownFactsUrl, knownFactsCombined).map { response =>
      response.status match {
        case OK => KnownFactsReturn(utr.value, knownFactsResult = true)
        case _ => KnownFactsReturn(utr.value, knownFactsResult = false)
      }
    }.recover {
      case exception =>
        logger.error("[EnrolmentStoreProxyConnector][queryKnownFacts] Enrolment Store Proxy error for queryKnownFacts", exception)
        KnownFactsReturn(utr.value, knownFactsResult = false)
    }
  }

}
