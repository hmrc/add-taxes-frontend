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

package utils

import javax.inject.{Inject, Singleton}
import play.api.mvc.Request
import play.api.mvc.Call

@Singleton
class Navigator @Inject()(urlHelper: UrlHelper) {

  def nextPage[A, B](id: A, b: B)(implicit ev: NextPage[A, B], request: Request[_]): Call =
    ev.get(b)(urlHelper, request)

}
