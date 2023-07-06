package models

import base.SpecBase
import config.featureToggles.FeatureSwitch.{ARSContentSwitch, AtarSwitch, NewCTCEnrolmentForNCTSJourney}
import models.other.importexports.DoYouWantToAddImportExport
import models.other.importexports.DoYouWantToAddImportExport.{ATaR, eBTI, _}
import models.requests.ServiceInfoRequest
import utils.Enrolments.CommonTransitConvention
import utils.RadioOption

class DoYouWantToImportExportSpec extends SpecBase {

  val expectedFullDoYouWantToAddImportExportList: List[DoYouWantToAddImportExport] = List(
    ATaR,
    ARS,
    EMCS,
    ICS,
    DDES,
    NOVA,
    NCTS,
    eBTI,
    NES,
    ISD
  )

  implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq())

  "values" must {

    "contain complete list of DoYouWantToAddImportExport enrolments" in {

      val expectedResult = expectedFullDoYouWantToAddImportExportList
      val actualResult = DoYouWantToAddImportExport.values

      expectedResult mustBe actualResult
    }
  }

  "RadioFilters" when {

    "removeATARIfNotEnabled" when {

      "AtarSwitch is enabled" must {

        "return Seq()" in {

          enable(AtarSwitch)

          val expectedResult = Seq()
          val actualResult = RadioFilters.removeATARIfNotEnabled()

          expectedResult mustBe actualResult
        }
      }

      "AtarSwitch is disabled" must {

        "return Seq(ATAR)" in {

          disable(AtarSwitch)

          val expectedResult = Seq(ATaR)
          val actualResult = RadioFilters.removeATARIfNotEnabled()

          expectedResult mustBe actualResult
        }
      }
    }

    "replaceATARWithARSIfEnabled" when {

      "ARSContentSwitch is enabled" must {

        "return ATAR" in {

          enable(ARSContentSwitch)

          val expectedResult = Seq(ATaR)
          val actualResult = RadioFilters.replaceATARWithARSIfEnabled()

          expectedResult mustBe actualResult
        }
      }
    }

    "replaceATARWithARSIfEnabled" when {

      "ARSContentSwitch is disabled" must {

        "return ARS" in {

          disable(ARSContentSwitch)

          val expectedResult = Seq(ARS)
          val actualResult = RadioFilters.replaceATARWithARSIfEnabled()

          expectedResult mustBe actualResult
        }
      }
    }

    "removeNCTSIfUserHasCTCEnrolment" when {

      "NewCTCEnrolmentForNCTSJourney is enabled" when {

        "user has CTC enrolment" must {

          "return NCTS enrolment" in {

            enable(NewCTCEnrolmentForNCTSJourney)

            implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

            val expectedResult = Seq(NCTS)
            val actualResult = RadioFilters.removeNCTSIfUserHasCTCEnrolment()

            expectedResult mustBe actualResult
          }
        }

        "user does not have CTC enrolment" must {

          "return Seq()" in {

            enable(NewCTCEnrolmentForNCTSJourney)

            val expectedResult = Seq()
            val actualResult = RadioFilters.removeNCTSIfUserHasCTCEnrolment()

            expectedResult mustBe actualResult
          }
        }
      }

      "NewCTCEnrolmentForNCTSJourney is disabled" when {

        "user has CTC enrolment" must {

          "return NCTS enrolment" in {

            disable(NewCTCEnrolmentForNCTSJourney)

            implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

            val expectedResult = Seq()
            val actualResult = RadioFilters.removeNCTSIfUserHasCTCEnrolment()

            expectedResult mustBe actualResult
          }
        }

        "user does not have CTC enrolment" must {

          "return Seq()" in {

            disable(NewCTCEnrolmentForNCTSJourney)

            val expectedResult = Seq()
            val actualResult = RadioFilters.removeNCTSIfUserHasCTCEnrolment()

            expectedResult mustBe actualResult
          }
        }
      }
    }
  }

  "filterRadios" when {

    "AtarSwitch is enabled" when {

      "ARSContentSwitch is enabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR and NCTS" in {

              enable(AtarSwitch)
              enable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR, NCTS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR" in {

              enable(AtarSwitch)
              enable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR" in {

              enable(AtarSwitch)
              enable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR" in {

              enable(AtarSwitch)
              enable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }
      }

      "ARSContentSwitch is disabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR and NCTS" in {

              enable(AtarSwitch)
              disable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ARS, NCTS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR" in {

              enable(AtarSwitch)
              disable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ARS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR" in {

              enable(AtarSwitch)
              disable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ARS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ARS" in {

              enable(AtarSwitch)
              disable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ARS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }
      }
    }

    "AtarSwitch is disabled" when {

      "ARSContentSwitch is enabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR and NCTS" in {

              disable(AtarSwitch)
              enable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR, NCTS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR" in {

              disable(AtarSwitch)
              enable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR" in {

              disable(AtarSwitch)
              enable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR" in {

              disable(AtarSwitch)
              enable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }
      }

      "ARSContentSwitch is disabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR, ARS and NCTS" in {

              disable(AtarSwitch)
              disable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR, ARS, NCTS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR, ARS" in {

              disable(AtarSwitch)
              disable(ARSContentSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR, ARS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment" must {

            "remove eBTI, ATAR, ARS" in {

              disable(AtarSwitch)
              disable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR, ARS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC enrolment" must {

            "remove eBTI, ATAR, ARS" in {

              disable(AtarSwitch)
              disable(ARSContentSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR, ARS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }
      }
    }
  }

  "options" when {

    "all enrolments are present" when {

      "AtarSwitch, ARSContentSwitch and NewCTCEnrolmentForNCTSJourney are enabled" when {

        "user has CTC enrolment" must {

          "Not return eBTI, ATAR or NCTS radio options" in {

            enable(AtarSwitch)
            enable(ARSContentSwitch)
            enable(NewCTCEnrolmentForNCTSJourney)

            implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

            val enrolmentsToRemove = List(eBTI, ATaR, NCTS)
            val filteredEnrolments = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))

            val expectedResult: Seq[RadioOption] = filteredEnrolments.map { enrolment =>
              RadioOption("doYouWantToAddImportExport", enrolment.toString)
            }

            val actualResult: Seq[RadioOption] = DoYouWantToAddImportExport.options()

            expectedResult mustBe actualResult
          }
        }

        "AtarSwitch, ARSContentSwitch and NewCTCEnrolmentForNCTSJourney are enabled" when {

          "Not return eBTI, ATAT or ARS radio options" in {

            disable(AtarSwitch)
            disable(ARSContentSwitch)
            disable(NewCTCEnrolmentForNCTSJourney)

            val enrolmentsToRemove = List(eBTI, ARS, ATaR)
            val filteredEnrolments = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))

            val expectedResult: Seq[RadioOption] = filteredEnrolments.map { enrolment =>
              RadioOption("doYouWantToAddImportExport", enrolment.toString)
            }

            val actualResult: Seq[RadioOption] = DoYouWantToAddImportExport.options()

            expectedResult mustBe actualResult
          }
        }
      }
    }
  }
}
