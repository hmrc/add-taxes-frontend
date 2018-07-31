#!/bin/bash

echo "Applying migration DoYouNeedToStopMGDController"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /gambling/how-to-stop-mgd                  controllers.deenrolment.DoYouNeedToStopMGDControllerController.onPageLoad()" >> ../conf/app.routes
echo "POST       /gambling/how-to-stop-mgd                  controllers.deenrolment.DoYouNeedToStopMGDControllerController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopMGDController" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopMGDController.title = Do you need to stop using the Machine Games Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopMGDController.heading = Do you need to stop using the Machine Games Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopMGDController.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopMGDController.No = No, I need to deregister from Machine Games Duty" >> ../conf/messages.en
echo "doYouNeedToStopMGDController.error.required = Select yes if you need to stop using the Machine Games Duty online service" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopMGDController" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopMGDController.title = A oes angen i chi roi gorau i ddefnyddio’r gwasanaeth ar-lein Toll Peiriannau Hapchwarae?" >> ../conf/messages.cy
echo "doYouNeedToStopMGDController.heading = A oes angen i chi roi gorau i ddefnyddio’r gwasanaeth ar-lein Toll Peiriannau Hapchwarae?" >> ../conf/messages.cy
echo "doYouNeedToStopMGDController.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToStopMGDController.No = Na, mae angen i fi ddadgofrestru o’r Doll Peiriannau Hapchwarae" >> ../conf/messages.cy
echo "doYouNeedToStopMGDController.error.required = Dewiswch Iawn os oes angen i chi roi’r gorau i ddefnyddio’r Doll Peiriannau Hapchwarae" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopMGDControllerNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopMGDController completed"
