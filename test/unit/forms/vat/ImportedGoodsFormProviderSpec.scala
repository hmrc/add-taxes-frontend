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

package forms.vat

import base.SpecBase
import forms.behaviours.FormBehaviours
import models._
import models.vat._
import play.api.i18n.Messages
import play.api.mvc.Request
import service.ThresholdService

class ImportedGoodsFormProviderSpec extends FormBehaviours with SpecBase {

  val validData: Map[String, String] = Map(
    "value" -> ImportedGoods.options.head.value
  )

  val thresholdService: ThresholdService = injector.instanceOf[ThresholdService]
  implicit val request: Request[_] = fakeRequest
  implicit val msg: Messages = messages
  val form = new ImportedGoodsFormProvider()(thresholdService.formattedVatThreshold())

  "ImportedGoods form" must {

    behave like questionForm[ImportedGoods](ImportedGoods.values.head)

    behave like formWithOptionField(
      Field("value", Required -> messages("importedGoods.error.required", thresholdService.formattedVatThreshold()), Invalid -> "error.invalid"),
      ImportedGoods.options.toSeq.map(_.value): _*)
  }
}
