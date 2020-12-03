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
import models.sa.{KnownFacts, SaEnrolment}
import play.api.test.Helpers.OK

object StubEnrolmentStoreConnector extends StubHelper {

  def withResponseForCheckUtr(utr: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(s"/enrolment-store-proxy/enrolment-store/enrolments/IR-SA~UTR~$utr/users?type=all", status, optBody)

  def withResponseForCheckEmpRef(officeNumber: String, payeReference: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(
      s"/enrolment-store-proxy/enrolment-store/enrolments/IR-PAYE~TaxOfficeNumber~$officeNumber~TaxOfficeReference~$payeReference/users?type=principal",
      status,
      optBody
    )

  def withResponseForCheckGroupSA(groupId: String)(status: Int, optBody: Option[String]): Unit =
    stubGet(s"/enrolment-store-proxy/enrolment-store/groups/$groupId/enrolments?type=principal&service=IR-SA", status, optBody)

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

  val es0ResponsePrincipals  =
    """
      |{
      |    "principalUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ],
      |    "delegatedUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ]
      |}
      |""".stripMargin

  val es0ResponseDelegated =
    """
      |{
      |    "principalUserIds": [],
      |    "delegatedUserIds": [
      |       "ABCEDEFGI1234567",
      |       "ABCEDEFGI1234568"
      |    ]
      |}
      |""".stripMargin

  val es3ResponseEnrolmentsWithSA =
    """
      |{
      |    "startRecord": 1,
      |    "totalRecords": 2,
      |    "enrolments": [
      |    {
      |           "service": "IR-CT",
      |           "state": "Activated",
      |           "friendlyName": "My Second Client's SA Enrolment",
      |           "enrolmentDate": "2017-06-25T12:24:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2017-07-01T09:52:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "9876543210"
      |              }
      |           ]
      |        },
      |        {
      |           "service": "IR-SA",
      |           "state": "Activated",
      |           "friendlyName": "My First Client's SA Enrolment",
      |           "enrolmentDate": "2018-10-05T14:48:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2018-10-13T17:36:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "1234567890"
      |              }
      |           ]
      |        },
      |        {
      |           "service": "IR-CT",
      |           "state": "Activated",
      |           "friendlyName": "My Second Client's SA Enrolment",
      |           "enrolmentDate": "2017-06-25T12:24:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2017-07-01T09:52:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "9876543210"
      |              }
      |           ]
      |        }
      |    ]
      |}
      |""".stripMargin

  val es3ResponseEnrolmentsWithOutSa =
    """
      |{
      |    "startRecord": 1,
      |    "totalRecords": 2,
      |    "enrolments": [
      |        {
      |           "service": "IR-PAYE",
      |           "state": "Activated",
      |           "friendlyName": "My First Client's SA Enrolment",
      |           "enrolmentDate": "2018-10-05T14:48:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2018-10-13T17:36:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "1234567890"
      |              }
      |           ]
      |        },
      |        {
      |           "service": "IR-CT",
      |           "state": "Activated",
      |           "friendlyName": "My Second Client's SA Enrolment",
      |           "enrolmentDate": "2017-06-25T12:24:00.000Z",
      |           "failedActivationCount": 1,
      |           "activationDate": "2017-07-01T09:52:00.000Z",
      |           "identifiers": [
      |              {
      |                 "key": "UTR",
      |                 "value": "9876543210"
      |              }
      |           ]
      |        }
      |    ]
      |}
      |""".stripMargin

  def successfulCheckUtrOkResponsePrincipal(utr: String) = withResponseForCheckUtr(utr)(OK, Some(es0ResponsePrincipals))
  def successfulCheckUtrOkResponseDelegated(utr: String) = withResponseForCheckUtr(utr)(OK, Some(es0ResponseDelegated))
  def checkUtrNoContentResponse(utr: String) = withResponseForCheckUtr(utr)(NO_CONTENT, None)
  def unsuccessfulCheckUtrResponse(utr: String) = withResponseForCheckUtr(utr)(INTERNAL_SERVER_ERROR, None)

  def successfulExistingEmpRefOkResponse(officeNumber: String, payeReference: String) =
    withResponseForCheckEmpRef(officeNumber, payeReference)(OK, None)
  def unsuccessfulExistingEmpRefNotFoundResponse(officeNumber: String, payeReference: String) =
    withResponseForCheckEmpRef(officeNumber, payeReference)(NOT_FOUND, None)
  def unsuccessfulExistingEmpRefResponse(officeNumber: String, payeReference: String) =
    withResponseForCheckEmpRef(officeNumber, payeReference)(INTERNAL_SERVER_ERROR, None)

  def checkGroupSAResponseOK(groupId: String, body: String) =
    withResponseForCheckGroupSA(groupId)(OK, Some(body))

  def checkGroupSAResponseNoContent(groupId: String) =
    withResponseForCheckGroupSA(groupId)(NO_CONTENT, None)

  def checkGroupSAResponseInternalServerError(groupId: String) =
    withResponseForCheckGroupSA(groupId)(INTERNAL_SERVER_ERROR, None)

  def successfulQueryKnownFacts(postBody: String) = withResponseForQueryKnownFacts(postBody)(OK, None)

  def notMatchedQueryKnownFacts(postBody: String) = withResponseForQueryKnownFacts(postBody)(
    NO_CONTENT, None
  )

  def verifyCheckUtr(count: Int, utr: String): Unit =
    verify(count, getRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/enrolments/IR-SA~UTR~$utr/users?type=all")))

  def verifyCheckGroupSA(count: Int, groupId: String): Unit =
    verify(count, getRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/groups/$groupId/enrolments?type=principal&service=IR-SA")))

  def verifyExistingEmpRef(count: Int, officeNumber: String, payeReference: String): Unit =
    verify(count, getRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/enrolments/IR-PAYE~TaxOfficeNumber~$officeNumber~TaxOfficeReference~$payeReference/users?type=principal")))

  def verifyQueryKnownFacts(count: Int): Unit =
    verify(count, postRequestedFor(urlEqualTo(s"/enrolment-store-proxy/enrolment-store/enrolments")))

}
