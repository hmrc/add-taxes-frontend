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

package support.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import controllers.Assets._
import models.sa.{KnownFacts, SAUTR, SaEnrolment}
import play.api.test.Helpers.OK

object StubEnrolmentStoreConnector extends StubHelper {



  def withResponseForCheckUtr(utr: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(s"/enrolment-store-proxy/enrolment-store/enrolments/IR-SA~UTR~$utr/users?type=principal", status, optBody)

  def withResponseForCheckEmpRef(officeNumber: String, payeReference: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(
      s"/enrolment-store-proxy/enrolment-store/enrolments/IR-PAYE~TaxOfficeNumber~$officeNumber~TaxOfficeReference~$payeReference/users?type=principal",
      status,
      optBody
    )

  def queryKnownFactsAllDataPost(utr: String, kf: KnownFacts) =
    s"""
       |{
       |"service" : "IR-SA",
       |"knownFacts": [
       |  {
       |    "key": "UTR",
       |    "value": "$utr"
       |  },
       |  {
       |    "key": "NINO",
       |    "value": "${kf.nino.get}"
       |  },
       |  {
       |    "key": "Postcode",
       |    "value":"${kf.postcode.get}"
       |  }
       | ]
       |}""".stripMargin

  def queryKnownFactsOnlyNinoPost(utr: String, kf: KnownFacts) =
    s"""
       |{
       |"service" : "IR-SA",
       |"knownFacts": [
       |  {
       |    "key": "UTR",
       |    "value": "$utr"
       |  },
       |  {
       |    "key": "NINO",
       |    "value": "${kf.nino.get}"
       |  }
       | ]
       |}""".stripMargin

  def queryKnownFactsOnlyPostCodePost(utr: String, kf: KnownFacts) =
    s"""
       |{
       |"service" : "IR-SA",
       |"knownFacts": [
       |  {
       |    "key": "UTR",
       |    "value": "$utr"
       |  },
       |  {
       |    "key": "Postcode",
       |    "value":"${kf.postcode.get}"
       |  }
       | ]
       |}""".stripMargin

  def withResponseForQueryKnownFacts(postBody: String)(status: Int, optBody: Option[String]): Unit =
    stubPost("/enrolment-store-proxy/enrolment-store/enrolments", status, postBody, optBody)

  def enrolForSaPost(saEnrolment: SaEnrolment) =
    s"""
       |{
       |  "userId": "${saEnrolment.userId}",
       |  "type": "principal",
       |  "action": "enrolAndActivate"
       |}""".stripMargin

  def withResponseForEnrolForSa(saEnrolment: SaEnrolment, utr: String, groupId: String)(status: Int, optBody: Option[String]): Unit =
    stubPost(s"/enrolment-store-proxy/enrolment-store/groups/$groupId/enrolments/IR-SA~UTR~$utr", status, enrolForSaPost(saEnrolment), optBody)

  def successfulCheckUtrOkResponse(utr: String) = withResponseForCheckUtr(utr)(OK, None)
  def unsuccessfulCheckUtrNotFoundResponse(utr: String) = withResponseForCheckUtr(utr)(NOT_FOUND, None)
  def unsuccessfulCheckUtrResponse(utr: String) = withResponseForCheckUtr(utr)(INTERNAL_SERVER_ERROR, None)

  def successfulExistingEmpRefOkResponse(officeNumber: String, payeReference: String) =
    withResponseForCheckEmpRef(officeNumber, payeReference)(OK, None)
  def unsuccessfulExistingEmpRefNotFoundResponse(officeNumber: String, payeReference: String) =
    withResponseForCheckEmpRef(officeNumber, payeReference)(NOT_FOUND, None)
  def unsuccessfulExistingEmpRefResponse(officeNumber: String, payeReference: String) =
    withResponseForCheckEmpRef(officeNumber, payeReference)(INTERNAL_SERVER_ERROR, None)

  def successfulQueryKnownFacts(postBody: String) = withResponseForQueryKnownFacts(postBody)(OK, None)

  def notMatchedQueryKnownFacts(postBody: String) = withResponseForQueryKnownFacts(postBody)(
    NO_CONTENT, None
  )

  def successFulEnrolForSa(saEnrolment: SaEnrolment, utr: String, groupId: String) = withResponseForEnrolForSa(saEnrolment, utr, groupId)(
    CREATED, None
  )

  def verifyCheckUtr(count: Int, utr: String): Unit =
    verify(count, getRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/enrolments/IR-SA~UTR~$utr/users?type=principal")))

  def verifyExistingEmpRef(count: Int, officeNumber: String, payeReference: String): Unit =
    verify(count, getRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/enrolments/IR-PAYE~TaxOfficeNumber~$officeNumber~TaxOfficeReference~$payeReference/users?type=principal")))

  def verifyQueryKnownFacts(count: Int): Unit =
    verify(count, postRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/enrolments")))

  def verifyEnrolForSa(count: Int, groupId: String, utr: String): Unit =
    verify(count, postRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/groups/$groupId/enrolments/IR-SA~UTR~$utr")))

}
