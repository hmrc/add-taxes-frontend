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
import forms.other.importexports.DoYouWantToAddImportExportFormProvider
import models.other.importexports.DoYouWantToAddImportExport
import org.mockito.Mockito.{reset, when}
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.other.importexports.doYouWantToAddImportExport
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.scalatest.BeforeAndAfterEach
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.language.LanguageUtils
import utils.DateUtil

class DoYouWantToAddImportExportViewSpec extends ViewBehaviours with BeforeAndAfterEach {

  val messageKeyPrefix = "doYouWantToAddImportExport"
  val form = new DoYouWantToAddImportExportFormProvider()()

  val serviceInfoContent: Html = HtmlFormat.empty

  val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
  val mockServiceConfig = app.injector.instanceOf[ServicesConfig]
  val mockConfiguration = mock[Configuration]
  val mockLanguageUtils = mock[LanguageUtils]

  val timeDefault = LocalDateTime.now()
  val ebtiDefault = false

  def mockConfig(time: LocalDateTime,
                ebtiBoolean: Boolean): FrontendAppConfig =
    new FrontendAppConfig(mockServiceConfig, mockConfiguration, mockLanguageUtils){
      override def now(): LocalDateTime = time
      override lazy val ebtiRemovalFeatureToggle = ebtiBoolean
    }

    def createView: () => HtmlFormat.Appendable = () =>
      new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(mockConfig(timeDefault, ebtiDefault), form)(serviceInfoContent)(fakeRequest, messages)

    def createViewUsingForm: (Form[_], LocalDateTime, Boolean) => HtmlFormat.Appendable = (form: Form[_], time: LocalDateTime, ebtiBoolean: Boolean) =>
      new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(mockConfig(time, ebtiBoolean), form)(serviceInfoContent)(fakeRequest, messages)

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
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form, timeDefault, ebtiDefault))
        for (option <- DoYouWantToAddImportExport.options(mockConfig(timeDefault, ebtiDefault))) {
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
        }
      }
    }

    for (option <- DoYouWantToAddImportExport.options(mockConfig(timeDefault, ebtiDefault))) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}")), timeDefault, ebtiDefault))
          assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

          for (unselectedOption <- DoYouWantToAddImportExport.options(mockConfig(timeDefault, ebtiDefault)).filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> "")), timeDefault, ebtiDefault))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
  "Feature flag for eBTI" should {
    "rendered" must {
      "display the eBTI option before the config date" in {
        val optionTime: LocalDateTime = LocalDateTime.parse("202012312259", formatter)

        val doc = asDocument(createViewUsingForm(form, optionTime, ebtiDefault))
        for (option <- DoYouWantToAddImportExport.options(mockConfig(optionTime, ebtiDefault))) {
          assertContainsRadioButton(doc, "doYouWantToAddImportExport.eBTI", "value", "eBTI", isChecked = false)
        }
      }

      "not display the eBTI option after the config date" in {
        val optionTime: LocalDateTime = LocalDateTime.parse("202012312300", formatter)

        val doc = asDocument(createViewUsingForm(form, optionTime, ebtiDefault))
        for (option <- DoYouWantToAddImportExport.options(mockConfig(optionTime, ebtiDefault))) {
          assertNotRenderedById(doc, "doYouWantToAddImportExport.eBTI")
        }
      }
    }
}}
