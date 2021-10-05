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

import javax.inject.Inject
import models.sa.{CredIdFound, DoYouHaveSAUTR, EnrolmentCheckResult, GroupIdFound, KnownFacts, KnownFactsReturn, NoRecordFound, NoSaUtr, SelectSACategory}
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.DataEvent
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuditService @Inject()(auditConnector: AuditConnector) {

  final private val enrolmentChecker: String = "business-tax-account-check"
  final private val utrEvent: String = "UTR-check"
  final private val payeEvent: String = "PAYE-check"
  final private val saKnownFactsEvent: String = "SA-knownfacts-result-check"
  final private val selectSACategoryEvent: String = "select-SA-Category-check"


  def auditSA(credId: String, saUtr: String, enrolmentCheckResult: EnrolmentCheckResult)
             (implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[_]): Future[AuditResult] = {

    val recordMatch: Boolean = enrolmentCheckResult match {
      case CredIdFound   => true
      case GroupIdFound  => true
      case NoSaUtr       => true
      case NoRecordFound => false
    }

    val detail = Map[String,String](elems =
      "credId" -> credId,
      "utr" -> saUtr,
      "recordFound" -> recordMatch.toString
    )

    val data = DataEvent(
      enrolmentChecker,
      utrEvent,
      tags = buildTags(),
      detail = detail
    )
    auditConnector.sendEvent(data)
  }

  def auditSAKnownFacts(credId: String,
                        saUtr: String,
                        knownfacts: KnownFacts,
                        knownfactsResult: Boolean
                       )
             (implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[_]): Future[AuditResult] = {

    val knownFactsIdentifier = knownfacts match {
      case KnownFacts(Some(postcode),_, _) => postcode
      case KnownFacts(_,Some(nino), _) => nino
      case KnownFacts(_,_, Some(isAbroad)) => "isAbroad" + isAbroad
    }

    val knownFactsPassOrFail = if(knownfactsResult) {"pass"} else {"fail"}

    val detail = Map[String,String](elems =
      "credId" -> credId,
      "utr" -> saUtr,
      "knownFactsIdentifier" -> knownFactsIdentifier,
      "knownFactsResult" -> knownFactsPassOrFail
    )

    val data = DataEvent(
      enrolmentChecker,
      saKnownFactsEvent,
      tags = buildTags(),
      detail = detail
    )
    auditConnector.sendEvent(data)
  }

  def auditEPAYE(credId: String, epayeRef: String, recordMatch: Boolean)
             (implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[_]): Future[AuditResult] = {
    val detail = Map[String,String](elems =
      "credId" -> credId,
      "utr" -> epayeRef,
      "recordFound" -> recordMatch.toString
    )

    val data = DataEvent(
      enrolmentChecker,
      payeEvent,
      tags = buildTags(),
      detail = detail
    )
    auditConnector.sendEvent(data)
  }

  private def buildTags()(implicit hc: HeaderCarrier, request: Request[_]): Map[String, String] = {
    Map(
      "X-Request-Id" -> hc.requestId.map(_.value).getOrElse(""),
      "X-Session-Id" -> hc.sessionId.map(_.value).getOrElse(""),
      "path" -> request.path,
      "transactionName" -> request.path,
      "clientIP" -> hc.trueClientIp.getOrElse(""),
      "clientPort" -> hc.trueClientPort.getOrElse(""),
      "type" -> "Audit"
    )
  }

  def auditSelectSACategory(saType: SelectSACategory, doYouHaveSaUtr: DoYouHaveSAUTR, utr: String, credId: String, groupId: String)
                           (implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[_]): Future[AuditResult] ={

    val detail = Map[String,String](elems =
      "saType" -> saType.toString,
      "credId" -> credId,
      "doYouHaveSaUtr" -> doYouHaveSaUtr.toString,
      "utr" -> utr,
      "groupID" -> groupId,
    )
    val data = DataEvent(
      enrolmentChecker,
      selectSACategoryEvent,
      tags = buildTags(),
      detail = detail
    )
    auditConnector.sendEvent(data)
  }

}
