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

package views.employer

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.employer.directorsRegisterByPhone

class DirectorsRegisterByPhoneViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "directorsRegisterByPhone"

  def createView = () => directorsRegisterByPhone(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "DirectorsRegisterByPhone view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "contain heading ID" in {
      val doc = asDocument(createView())
      doc.getElementsByTag("h1").attr("id") mustBe "directors-register-by-phone"
    }

    def viewIncludes(s: String): Unit = asDocument(createView()).text() must include(s)

    "include paragraph 1" in {
      viewIncludes(" You need to contact PAYE helpline to register as an employer.")
    }

    "include header 1" in {
      viewIncludes("What you’ll need to provide")
    }

    "include list 1" in {
      viewIncludes("When you phone HMRC, you’ll need to provide information about your company, including:")
      viewIncludes("its name, registered address and phone number")
      viewIncludes("its trading name, if this is different")
      viewIncludes("the type of business, for example plumbing, investment, electrical engineering")
      viewIncludes("its unique taxpayer reference (UTR)")
      viewIncludes("its company registration number")
      viewIncludes("the names and National Insurance numbers of all company directors - unless your company’s not " +
        "based in the UK, or any directors have been advised by the Department for Work and Pensions (DWP) that they " +
        "do not qualify for a number")
    }

    "include list 2" in {
      viewIncludes("You’ll also need to provide:")
      viewIncludes("your name, email address and a telephone number HMRC can contact you on")
      viewIncludes("a postal address for correspondence, if this is different to the company’s address")
    }

    "include list 3" in {
      viewIncludes("Finally, you’ll need to provide the following information about your employees:")
      viewIncludes("the date of their first payday, or when you first provide expenses or benefits if this is earlier")
      viewIncludes("how many people you’re employing - or expect to employ in this tax year")
      viewIncludes("whether you’ll be using any subcontractors under the construction industry scheme(CIS)")
      viewIncludes("whether you’ll be operating an occupational pension scheme")
    }

    "include header 2" in {
      viewIncludes("What happens next")
    }

    "include paragraph 2" in {
      viewIncludes("Once you’ve registered, you’ll get a letter containing your PAYE and Accounts Office references. " +
        "You’ll need these to report and pay PAYE tax and National Insurance to HMRC. This normally arrives within 10 " +
        "working days.")
    }

    "include paragraph 3" in {
      viewIncludes("Finally, you’ll need to enrol for PAYE Online before you can send payroll information to HMRC.")
    }

    "include link 1" in {
      val doc = asDocument(createView())
      assertLinkByContent(
        doc,
        "contact PAYE helpline",
        "https://www.gov.uk/government/organisations/hm-revenue-customs/contact/employer-enquiries-support-for-new-employers")
    }

    "include link 2" in {
      val doc = asDocument(createView())
      assertLinkByContent(doc, "expenses or benefits", "https://www.gov.uk/employer-reporting-expenses-benefits")
    }

    "include link 3" in {
      val doc = asDocument(createView())
      assertLinkById(
        doc,
        "construction_industry_link",
        "construction industry scheme(CIS)",
        "https://www.gov.uk/what-is-the-construction-industry-scheme",
        "")
    }

    "include link 4" in {
      val doc = asDocument(createView())
      assertLinkByContent(doc, "enrol for PAYE Online", "https://www.gov.uk/paye-online")
    }
  }
}
