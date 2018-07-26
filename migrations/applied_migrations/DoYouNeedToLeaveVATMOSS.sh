#!/bin/bash

echo "Applying migration DoYouNeedToLeaveVATMOSS"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/how-to-stop-vat-moss                  controllers.deenrolment.DoYouNeedToLeaveVATMOSSController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/how-to-stop-vat-moss                  controllers.deenrolment.DoYouNeedToLeaveVATMOSSController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToLeaveVATMOSS" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToLeaveVATMOSS.title = Do you need to leave this VAT MOSS scheme?" >> ../conf/messages.en
echo "doYouNeedToLeaveVATMOSS.heading = Do you need to leave this VAT MOSS scheme?" >> ../conf/messages.en
echo "doYouNeedToLeaveVATMOSS.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToLeaveVATMOSS.No = No, I need to stop using the online service" >> ../conf/messages.en
echo "doYouNeedToLeaveVATMOSS.error.required = Select yes if you need to leave this VAT MOSS scheme" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToLeaveVATMOSS" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToLeaveVATMOSS.title = A oes angen i chi adael y cynllun Gwasanaeth Un Cam ar gyfer TAW hwn?" >> ../conf/messages.cy
echo "doYouNeedToLeaveVATMOSS.heading = A oes angen i chi adael y cynllun Gwasanaeth Un Cam ar gyfer TAW hwn?" >> ../conf/messages.cy
echo "doYouNeedToLeaveVATMOSS.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToLeaveVATMOSS.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein" >> ../conf/messages.cy
echo "doYouNeedToLeaveVATMOSS.error.required = Dewiswch Iawn os oes angen i chi adael y cynllun Gwasanaeth Un Cam ar gyfer TAW hwn" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToLeaveVATMOSSNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToLeaveVATMOSS completed"
