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

package utils.nextpage.sa

import config.FrontendAppConfig
import identifiers.EnterSAUTRId
import play.api.mvc.{Call, Request}
import utils.NextPage
import models.sa.SAUTR

trait EnterSAUTRNextPage {

  implicit val enterSAUTR: NextPage[EnterSAUTRId.type, SAUTR, Call] = {

    new NextPage[EnterSAUTRId.type, SAUTR, Call] {
      override def get(b: SAUTR)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call = ???
    }

  }

}
