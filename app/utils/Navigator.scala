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

package utils

import javax.inject.{Inject, Singleton}
import config.FrontendAppConfig
import models.requests.ServiceInfoRequest

@Singleton
class Navigator[C] @Inject()(frontendAppConfig: FrontendAppConfig) {

  def nextPage[A, B](id: A, b: B)(implicit ev: NextPage[A, B, C], request: ServiceInfoRequest[_]): C =
    ev.get(b)(frontendAppConfig, request)

}
