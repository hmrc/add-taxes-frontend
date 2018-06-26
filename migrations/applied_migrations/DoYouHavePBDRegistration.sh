#!/bin/bash

echo "Applying migration DoYouHavePBDRegistration"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        other/gambling/pbd                  controllers.other.gambling.pbd.DoYouHavePBDRegistrationController.onPageLoad()" >> ../conf/app.routes
echo "POST       other/gambling/pbd                  controllers.other.gambling.pbd.DoYouHavePBDRegistrationController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHavePBDRegistration" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHavePBDRegistration.title = Do you have a Pool Betting Duty registration number?" >> ../conf/messages.en
echo "doYouHavePBDRegistration.heading = Do you have a Pool Betting Duty registration number?" >> ../conf/messages.en
echo "doYouHavePBDRegistration.Yes = Yes" >> ../conf/messages.en
echo "doYouHavePBDRegistration.No = No" >> ../conf/messages.en
echo "doYouHavePBDRegistration.error.required = Select yes if you have a Pool Betting Duty registration number" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHavePBDRegistration" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHavePBDRegistration.title = A oes gennych rif cofrestru Toll Cronfa Fetio?" >> ../conf/messages.cy
echo "doYouHavePBDRegistration.heading = A oes gennych rif cofrestru Toll Cronfa Fetio?" >> ../conf/messages.cy
echo "doYouHavePBDRegistration.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHavePBDRegistration.No = Na" >> ../conf/messages.cy
echo "doYouHavePBDRegistration.error.required = Dewiswch Iawn os oes gennych rif cofrestru ar gyfer Toll Cronfa Fetio" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.other.gambling.pbd.DoYouHavePBDRegistrationNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHavePBDRegistration completed"
