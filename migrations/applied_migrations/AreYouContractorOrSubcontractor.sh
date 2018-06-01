#!/bin/bash

echo "Applying migration AreYouContractorOrSubcontractor"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis/uk                  controllers.employer.cis.uk.AreYouContractorOrSubcontractorController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis/uk                  controllers.employer.cis.uk.AreYouContractorOrSubcontractorController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AreYouContractorOrSubcontractor" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "areYouContractorOrSubcontractor.title = Are you a contractor or subcontractor?" >> ../conf/messages.en
echo "areYouContractorOrSubcontractor.heading = Are you a contractor or subcontractor?" >> ../conf/messages.en
echo "areYouContractorOrSubcontractor.option1 = areYouContractorOrSubcontractor" Option 1 >> ../conf/messages.en
echo "areYouContractorOrSubcontractor.option2 = areYouContractorOrSubcontractor" Option 2 >> ../conf/messages.en
echo "areYouContractorOrSubcontractor.error.required = Select if you are a contractor or subcontractor" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AreYouContractorOrSubcontractor" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "areYouContractorOrSubcontractor.title = e]: Ydych chi’n gontractwr neu’n isgontractwr?" >> ../conf/messages.cy
echo "areYouContractorOrSubcontractor.heading = Ydych chi’n gontractwr neu’n isgontractwr?" >> ../conf/messages.cy
echo "areYouContractorOrSubcontractor.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "areYouContractorOrSubcontractor.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "areYouContractorOrSubcontractor.error.required = Dewiswch p’un a ydych yn gontractwr neu’n isgontractwr" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.cis.uk.AreYouContractorOrSubcontractorNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AreYouContractorOrSubcontractor completed"
