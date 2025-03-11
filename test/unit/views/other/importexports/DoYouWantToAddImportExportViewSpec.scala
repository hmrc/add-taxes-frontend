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
import config.featureToggles.FeatureSwitch.{AtarSwitch, CDSSwitch, NewCTCEnrolmentForNCTSJourney}
import forms.other.importexports.DoYouWantToAddImportExportFormProvider
import models.other.importexports.DoYouWantToAddImportExport
import models.requests.ServiceInfoRequest
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Configuration
import play.api.data.Form
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.language.LanguageUtils
import utils.Enrolments.{CommonTransitConvention, CustomsDeclarationServices}
import utils.RadioOption
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
  implicit val msgs: Messages = messages
  implicit val mockConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]
  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq())

  def createView: () => HtmlFormat.Appendable = () =>
    new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(form)(serviceInfoContent)

  def createViewUsingForm(form: Form[_])(implicit request: ServiceInfoRequest[_]): HtmlFormat.Appendable = {
    new doYouWantToAddImportExport(formWithCSRF, mainTemplate)(form)(serviceInfoContent)
  }

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

    "AtarSwitch is enabled" when {

      "ARSContentSwitch is enabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch is enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {
                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDS switch is disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }


                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {
                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {
                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch is enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch is disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for all option and no filtered options" in {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with all value selected" when {

                  enable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each of the option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each of the value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each of the option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each of the value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each of the option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each of the value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each of the option and no filtered options" in {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each of the value selected" when {

                  enable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    "AtarSwitch is disabled" when {

      "ARSContentSwitch is enabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }


        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  enable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          "user does not have the CTC enrolment" must {

            "CDSSwitch enabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  enable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
            }

            "CDSSwitch disabled" when {

              "user has CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }

              "user does not have CDS enrolment" must {

                "contain radio buttons for each option and no filtered options" in {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  val doc = asDocument(createViewUsingForm(form))
                  for (option <- DoYouWantToAddImportExport.options()) {
                    assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = false)
                  }

                  val expectedFilteredEnrolments = DoYouWantToAddImportExport.values.diff(DoYouWantToAddImportExport.filteredRadios())
                  val expectedFilteredRadios = expectedFilteredEnrolments.map(value => RadioOption("doYouWantToAddImportExport", value.toString))

                  for (option <- expectedFilteredRadios) {
                    assertNotRenderedById(doc, option.id)
                  }
                }

                "render with each value selected" when {

                  disable(AtarSwitch)
                  disable(NewCTCEnrolmentForNCTSJourney)
                  disable(CDSSwitch)

                  for (option <- DoYouWantToAddImportExport.options()) {
                    s"rendered with a value of '${option.value}'" must {

                      s"have the '${option.value}' radio button selected" in {

                        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
                        assertContainsRadioButton(doc, option.id, "value", option.value, isChecked = true)

                        for (unselectedOption <- DoYouWantToAddImportExport.options().filterNot(o => o == option)) {
                          assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, isChecked = false)
                        }
                      }
                    }
                  }
                }
              }
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
}
