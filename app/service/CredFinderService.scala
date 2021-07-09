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

import connectors.{CitizensDetailsConnector, GetBusinessDetailsConnector}

import javax.inject.Inject
import models.DesignatoryDetails
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class CredFinderService @Inject()(citizensDetailsConnector: CitizensDetailsConnector, getBusinessDetailsConnector: GetBusinessDetailsConnector) {

  def utrCheck(enrolments: Set[Enrolment])(implicit hc: HeaderCarrier, ec: ExecutionContext) ={
    enrolments.map{
      enrolment => enrolment.key match {
        case "IR-SA" => val utr = enrolment.getIdentifier("utr")
          utr match {
            case Some(value) => utrStuff(value)
            case _ => None
          }

        case "HMRC-MTD-IT" => val mtdId = enrolment.getIdentifier("MTDITID").map(_.value).getOrElse("")
          mtdId
        }
      }

    }


  def utrStuff(utr: EnrolmentIdentifier)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    for {
      designatoryDetails <- citizensDetailsConnector.getDesignatoryDetails(utr.key, utr.value)

    } yield {
        designatoryDetailsStuff(designatoryDetails).map(x => x)
        }
      }


  def mtdIdStuff(mtdId: EnrolmentIdentifier) = {

  }

  def designatoryDetailsStuff(designatoryDetails: Option[DesignatoryDetails])(implicit hc: HeaderCarrier, ec: ExecutionContext) = {
    designatoryDetails match {
      case Some (details) => for(
        test <- getBusinessDetailsConnector.getBusinessDetails("nino", details.nino)

      ) yield {
        test.isDefined
        }
      }
     }
   }

