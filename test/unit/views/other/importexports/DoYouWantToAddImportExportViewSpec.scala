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

package views.other.importexports

import config.FrontendAppConfig
import config.featureToggles.FeatureSwitch.AtarSwitch
import config.featureToggles.FeatureToggleSupport.isEnabled
import forms.other.importexports.DoYouWantToAddImportExportFormProvider
import models.other.importexports.DoYouWantToAddImportExport
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.language.LanguageUtils
import views.behaviours.ViewBehaviours
import views.html.other.importexports.doYouWantToAddImportExport

import java.time.format.DateTimeFormatter

class DoYouWantToAddImportExportViewSpec extends ViewBehaviours with BeforeAndAfterEach {

  val messageKeyPrefix = "doYouWantToAddImportExport"
  val form = new DoYouWantToAddImportExportFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
  val mockServiceConfig: ServicesConfig = app.injector.instanceOf[ServicesConfig]
  val mockConfiguration: Configuration = mock[Configuration]
  val mockLanguageUtils: LanguageUtils = mock[LanguageUtils]
  val mockConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

    def createView: () => HtmlFormat.Appendable = () =>
      new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(mockConfig, form, isEnabled(AtarSwitch))(serviceInfoContent)(fakeRequest, messages)

    def createViewUsingForm: (Form[_]) => HtmlFormat.Appendable = (form: Form[_]) =>
      new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(mockConfig, form, isEnabled(AtarSwitch))(serviceInfoContent)(fakeRequest, messages)

  override def beforeEach(): Unit = {
    reset(mockConfiguration)
    reset(mockLanguageUtils)
    super.beforeEach()
  }

  "DoYouWantToAddImportExport view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "do-you-want-to-add-import-export"
    }
  }

  "DoYouWantToAddImportExport view" when {
    val atarBool: Boolean = isEnabled(AtarSwitch)

    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouWantToAddImportExport.options(atarBool)) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- DoYouWantToAddImportExport.options(atarBool)) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- DoYouWantToAddImportExport.options(atarBool).filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
  "eBTI" should {
    "rendered" must {
      "not display the eBTI option" in {
        val atarBool: Boolean = isEnabled(AtarSwitch)
        val doc = asDocument(createViewUsingForm(form))
        for (option <- DoYouWantToAddImportExport.options(atarBool)) {
          assertNotRenderedById(doc, "doYouWantToAddImportExport.eBTI")
        }
      }
    }
}}
