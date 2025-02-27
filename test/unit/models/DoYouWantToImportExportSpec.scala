package models

import base.SpecBase
import config.featureToggles.FeatureSwitch.{AtarSwitch, NewCTCEnrolmentForNCTSJourney}
import models.other.importexports.DoYouWantToAddImportExport
import models.other.importexports.DoYouWantToAddImportExport.{ATaR, eBTI, _}
import models.requests.ServiceInfoRequest
import utils.Enrolments.{CommonTransitConvention, CustomsDeclarationServices}
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
    ISD,
    CDS
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

    "replaceATARWithARSIfEnabled" must {

        "return ATAR" in {

          val expectedResult = Seq(ATaR)
          val actualResult = RadioFilters.replaceATARWithARSIfEnabled()

          expectedResult mustBe actualResult
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

      "removeCDSIfUserHasEnrolment" when {

        "user has CDS enrolment" must {

          "return Seq(CDS)" in {

            implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


            val expectedResult = Seq(CDS)
            val actualResult = RadioFilters.removeCDSIfUserHasEnrolment()

            expectedResult mustBe actualResult
          }
        }

        "user doesnt have CDS enrolment" must {

          "return Seq()" in {

            implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

            val expectedResult = Seq()
            val actualResult = RadioFilters.removeCDSIfUserHasEnrolment()

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

          "user has CDS and CTS enrolment" must {

            "remove eBTI, ATAR, NCTS and CDS" in {

              enable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, NCTS, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CTC enrolment without CDS enrolment" must {

            "remove eBTI, ATAR, NCTS" in {

              enable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention))

              val enrolmentsToRemove = Seq(eBTI, ATaR, NCTS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTS enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              enable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user does not have CTC and CDS enrolment" must {

            "remove eBTI, ATAR" in {

              enable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              val enrolmentsToRemove = Seq(eBTI, ATaR)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC enrolment and CDS enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              enable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              enable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }
      }


        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC and CDS enrolment" must {

            "remove eBTI, ATAR, NCTS and CDS" in {

              enable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI,ATaR, NCTS, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATAR, CDS" in {

              enable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))


              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC and CDS enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              enable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATaR and CDS" in {

              enable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

    }

    "AtarSwitch is disabled" when {

      "ARSContentSwitch is enabled" must {

        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC and CDS enrolment" must {

            "remove eBTI, ATAR, NCTS and CDS" in {

              disable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, NCTS, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              disable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC and CDS enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              disable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              disable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }
      }


        "NewCTCEnrolmentForNCTSJourney is enabled" when {

          "user has CTC and CDS enrolment" must {

            "remove eBTI, ATAR, NCTS and CDS" in {

              disable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, NCTS, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATAR, and CDS" in {

              disable(AtarSwitch)
              enable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

        "NewCTCEnrolmentForNCTSJourney is disabled" when {

          "user has CTC and CDS enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              disable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CommonTransitConvention, CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }

          "user has CDS enrolment without CTC enrolment" must {

            "remove eBTI, ATAR and CDS" in {

              disable(AtarSwitch)
              disable(NewCTCEnrolmentForNCTSJourney)

              implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

              val enrolmentsToRemove = Seq(eBTI, ATaR, CDS)

              val expectedResult = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))
              val actualResult = DoYouWantToAddImportExport.filteredRadios()

              expectedResult mustBe actualResult
            }
          }
        }

    }
  }

  "options" when {

    "all enrolments are present" when {

      "AtarSwitch and NewCTCEnrolmentForNCTSJourney are enabled" when {

        "user has CTC enrolment" must {

          "Not return eBTI, ATAR or NCTS radio options" in {

            enable(AtarSwitch)
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

        "user has CDS enrolment" must {

          "Not return eBTI, ATaR or CDS radio options" in {

            enable(AtarSwitch)
            enable(NewCTCEnrolmentForNCTSJourney)

            implicit val request: ServiceInfoRequest[_] = reqWithEnrolments(Seq(CustomsDeclarationServices))

            val enrolmentsToRemove = List(eBTI, ATaR, CDS)

            val filteredEnrolments = expectedFullDoYouWantToAddImportExportList.filterNot(enrolmentsToRemove.contains(_))

            val expectedResult: Seq[RadioOption] = filteredEnrolments.map { enrolment =>
              RadioOption("doYouWantToAddImportExport", enrolment.toString)
            }

            val actualResult: Seq[RadioOption] = DoYouWantToAddImportExport.options()

            expectedResult mustBe actualResult
          }
        }

        "AtarSwitch and NewCTCEnrolmentForNCTSJourney are enabled" when {

          "Not return eBTI or ATAT radio options" in {

            disable(AtarSwitch)
            disable(NewCTCEnrolmentForNCTSJourney)

            val enrolmentsToRemove = List(eBTI, ATaR)
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
