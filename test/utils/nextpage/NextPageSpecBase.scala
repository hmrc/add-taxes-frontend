/*
 * Copyright 2018 HM Revenue & Customs
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

package utils.nextpage

import base.SpecBase
import models.requests.{AuthenticatedRequest, ServiceInfoRequest}
import play.api.mvc.AnyContent
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{Enrolment, Enrolments}
import utils.{HmrcEnrolmentType, NextPage}

trait NextPageSpecBase extends SpecBase {

  implicit val request = fakeRequest

  val serviceRequest: ServiceInfoRequest[AnyContent] =
    ServiceInfoRequest(AuthenticatedRequest(request, "", Enrolments(Set()), None), Html(""))

  val saEnrolment = Enrolment(key = HmrcEnrolmentType.SA.toString, identifiers = Seq(), state = "Activated")

  val ctEnrolment = Enrolment(key = HmrcEnrolmentType.CORP_TAX.toString, identifiers = Seq(), state = "Activated")

  val epayeEnrolment = Enrolment(key = HmrcEnrolmentType.EPAYE.toString, identifiers = Seq(), state = "Activated")

  def createServiceRequest(enrolments: Set[Enrolment]): ServiceInfoRequest[AnyContent] =
    ServiceInfoRequest(AuthenticatedRequest(request, "", Enrolments(enrolments), None), Html(""))

  def nextPage[A, B](np: NextPage[A, B], userSelection: B, urlRedirect: String): Unit =
    s"$userSelection is selected" should {
      s"redirect to $urlRedirect" in {
        val result = np.get(userSelection)
        result.url mustBe urlRedirect
      }
    }

  def nextPageWithEnrolments[A, B](
    np: NextPage[A, B],
    userSelectionWithEnrolments: B,
    userSelection: String,
    urlRedirect: String,
    enrolments: String): Unit =
    s"$userSelection is selected with $enrolments" should {
      s"redirect to $urlRedirect" in {
        val result = np.get(userSelectionWithEnrolments)
        result.url mustBe urlRedirect
      }
    }
}
