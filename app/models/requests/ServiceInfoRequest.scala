/*
 * Copyright 2024 HM Revenue & Customs
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

package models.requests

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.WrappedRequest
import play.twirl.api.Html
import utils.Enrolments

case class ServiceInfoRequest[A](request: AuthenticatedRequest[A], serviceInfoContent: Html)
  extends WrappedRequest[A](request) {

  def hasEnrolments(enrolments: Seq[Enrolments])(implicit request: ServiceInfoRequest[_]): Boolean = {
    val targetEnrolments = enrolments.map(_.toAuthEnrolment.key).toSet
    val userEnrolments = request.request.enrolments.enrolments.map(_.key)
    targetEnrolments.subsetOf(userEnrolments)
  }

  def doesNotHaveEnrolments(enrolments: Seq[Enrolments])(implicit request: ServiceInfoRequest[_]): Boolean = {
    val targetEnrolments = enrolments.map(_.toAuthEnrolment.key).toSet
    val userEnrolments = request.request.enrolments.enrolments.map(_.key)
    !targetEnrolments.subsetOf(userEnrolments)
  }
}

case class ListLinks(message: String, url: String, alerts: Option[String] = None, showBoolean: Option[Boolean] = Some(true))

case class NavLinks(en: String, cy: String, url: String, alerts: Option[Int] = None)

object NavLinks {
  implicit val format: OFormat[NavLinks] = Json.format[NavLinks]
}

case class NavContent(home: NavLinks,
                      account: NavLinks,
                      messages: NavLinks,
                      help: NavLinks,
                      forms: NavLinks
                     )

object NavContent {
  implicit val format: OFormat[NavContent] = Json.format[NavContent]
}
