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

package handlers

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import play.api.http.HeaderNames.CACHE_CONTROL
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results.NotFound
import play.api.mvc.{Request, RequestHeader, Result}
import play.twirl.api.Html
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import views.html.error_template

import scala.language.implicitConversions

@Singleton
class ErrorHandler @Inject()(appConfig: FrontendAppConfig,
                             val messagesApi: MessagesApi,
                             error_template: error_template) extends FrontendErrorHandler with I18nSupport {

  implicit def rhToRequest(rh: RequestHeader): Request[_] = Request(rh, "")

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: Request[_]): Html = {
    error_template(pageTitle, heading, message, appConfig)
  }

  override def resolveError(rh: RequestHeader, ex: Throwable): Result = ex match {
    case _: NotFoundException => NotFound(notFoundTemplate(rh)).withHeaders(CACHE_CONTROL -> "no-cache")
    case _                    => super.resolveError(rh, ex)
  }
}

