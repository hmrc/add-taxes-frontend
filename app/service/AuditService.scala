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

import javax.inject.Inject
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.DataEvent
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuditService @Inject()(auditConnector: AuditConnector) {

  final private val enrolmentChecker: String = "business-tax-account-check"
  final private val utrEvent: String = "UTR-check"

  def auditSA(credId: String, saUtr: String, recordMatch: Boolean)
             (implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[_]): Future[AuditResult] = {
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

}
