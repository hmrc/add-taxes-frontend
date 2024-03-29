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

package utils

import config.FrontendAppConfig
import models.requests.ServiceInfoRequest
import models.{Mode, NormalMode}

class FakeNavigator[C](desiredRoute: C, mode: Mode = NormalMode)(
  implicit config: FrontendAppConfig)
    extends Navigator[C](config) {

  override def nextPage[A, B](id: A, b: B)(implicit ev: NextPage[A, B, C], request: ServiceInfoRequest[_]): C = desiredRoute
}
