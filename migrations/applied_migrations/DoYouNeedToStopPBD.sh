#!/bin/bash

echo "Applying migration DoYouNeedToStopPBD"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /gambling/how-to-stop-pbd                  controllers.deenrolment.DoYouNeedToStopPBDController.onPageLoad()" >> ../conf/app.routes
echo "POST       /gambling/how-to-stop-pbd                  controllers.deenrolment.DoYouNeedToStopPBDController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopPBD" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopPBD.title = Do you need to stop using the Pool Betting Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopPBD.heading = Do you need to stop using the Pool Betting Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopPBD.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopPBD.No = No, I need to deregister from Pool Betting Duty" >> ../conf/messages.en
echo "doYouNeedToStopPBD.error.required = Select yes if you need to stop using the Pool Betting Duty online service" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopPBD" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopPBD.title = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Cronfa Fetio?" >> ../conf/messages.cy
echo "doYouNeedToStopPBD.heading = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Cronfa Fetio?" >> ../conf/messages.cy
echo "doYouNeedToStopPBD.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToStopPBD.No = Na, mae angen i fi ddadgofrestru o’r Doll Cronfa Fetio" >> ../conf/messages.cy
echo "doYouNeedToStopPBD.error.required = Dewiswch Iawn os oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Cronfa Fetio" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopPBDNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopPBD completed"
