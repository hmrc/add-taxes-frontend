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

package forms.other.importexports

import base.SpecBase
import config.FrontendAppConfig
import forms.behaviours.FormBehaviours
import models._
import models.other.importexports._
import models.requests.ServiceInfoRequest
import utils.RadioOption

class DoYouWantToAddImportExportFormProviderSpec extends SpecBase with FormBehaviours {
  implicit val config: FrontendAppConfig = frontendAppConfig
  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq())

  val validData: Map[String, String] = Map(
    "value" -> DoYouWantToAddImportExport.options().head.value
  )
  val form = new DoYouWantToAddImportExportFormProvider()()

  "DoYouWantToAddImportExport form" must {

    behave like questionForm[DoYouWantToAddImportExport](DoYouWantToAddImportExport.filteredRadios().head)

    val allDoYouWantToAddImportExportOptions: Seq[RadioOption] = DoYouWantToAddImportExport.values.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

    behave like formWithOptionField(
      Field("value", Required -> "doYouWantToAddImportExport.error.required", Invalid -> "error.invalid"),
      allDoYouWantToAddImportExportOptions.map(_.value): _*
    )
  }
}
