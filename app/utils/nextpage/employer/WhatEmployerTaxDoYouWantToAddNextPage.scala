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

package utils.nextpage.employer

import config.FrontendAppConfig
import identifiers.WhatEmployerTaxDoYouWantToAddId
import models.employer.WhatEmployerTaxDoYouWantToAdd
import play.api.mvc.{Call, Request}
import utils.NextPage

trait WhatEmployerTaxDoYouWantToAddNextPage {

  implicit val whatEmployerTaxDoYouWantToAdd
    : NextPage[WhatEmployerTaxDoYouWantToAddId.type, WhatEmployerTaxDoYouWantToAdd] = {
    new NextPage[WhatEmployerTaxDoYouWantToAddId.type, WhatEmployerTaxDoYouWantToAdd] {
      override def get(
        b: WhatEmployerTaxDoYouWantToAdd)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case WhatEmployerTaxDoYouWantToAdd.Option1 => ???
          case WhatEmployerTaxDoYouWantToAdd.Option2 => ???
          case WhatEmployerTaxDoYouWantToAdd.Option3 => ???
          case WhatEmployerTaxDoYouWantToAdd.Option4 => ???
          case WhatEmployerTaxDoYouWantToAdd.Option5 => ???
        }
    }
  }
}
