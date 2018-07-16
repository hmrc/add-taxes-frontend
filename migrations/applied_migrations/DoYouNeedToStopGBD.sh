#!/bin/bash

echo "Applying migration DoYouNeedToStopGBD"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /gambling/how-to-stop-gbd                  controllers.deenrolment.DoYouNeedToStopGBDController.onPageLoad()" >> ../conf/app.routes
echo "POST       /gambling/how-to-stop-gbd                  controllers.deenrolment.DoYouNeedToStopGBDController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopGBD" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopGBD.title = Do you need to stop using the General Betting Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopGBD.heading = Do you need to stop using the General Betting Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopGBD.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopGBD.No = No, I need to deregister from General Betting Duty" >> ../conf/messages.en
echo "doYouNeedToStopGBD.error.required = Select yes if you need to stop using the General Betting Duty online service" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopGBD" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopGBD.title = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Fetio Gyffredinol?" >> ../conf/messages.cy
echo "doYouNeedToStopGBD.heading = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Fetio Gyffredinol?" >> ../conf/messages.cy
echo "doYouNeedToStopGBD.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToStopGBD.No = Na, mae angen i fi ddadgofrestru o’r Doll Fetio Gyffredinol" >> ../conf/messages.cy
echo "doYouNeedToStopGBD.error.required = Dewiswch Iawn os oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Fetio Gyffredinol" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopGBDNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopGBD completed"
