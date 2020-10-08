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

package handlers

import config.FrontendAppConfig
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Request
import play.twirl.api.Html
import views.html.error_template
//
//class FakeErrorHandler(serverErrorTemplate: String,
//                       val messagesApi: MessagesApi,
//                       error_template: error_template)(implicit appConfig: FrontendAppConfig)
//  extends ErrorHandler with I18nSupport {
//
//  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit rh: Request[_]): Html = {
//    error_template(pageTitle, heading, message, appConfig)
//  }
//
//  override def internalServerErrorTemplate(implicit request: Request[_]): Html = Html(serverErrorTemplate)
//}
