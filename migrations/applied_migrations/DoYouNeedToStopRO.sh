#!/bin/bash

echo "Applying migration DoYouNeedToStopRO"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ro/how-to-stop-ro                  controllers.deenrolment.DoYouNeedToStopROController.onPageLoad()" >> ../conf/app.routes
echo "POST       /ro/how-to-stop-ro                  controllers.deenrolment.DoYouNeedToStopROController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopRO" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopRO.title = Do you need to stop using the Rebated Oils Enquiry online service?" >> ../conf/messages.en
echo "doYouNeedToStopRO.heading = Do you need to stop using the Rebated Oils Enquiry online service?" >> ../conf/messages.en
echo "doYouNeedToStopRO.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopRO.No = No, I need to deregister from the Rebated Oils scheme" >> ../conf/messages.en
echo "doYouNeedToStopRO.error.required = Select yes if you need to stop using the Rebated Oils Enquiry online service" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopRO" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopRO.title = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Ymholiadau Olew Ad-daliedig?" >> ../conf/messages.cy
echo "doYouNeedToStopRO.heading = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Ymholiadau Olew Ad-daliedig?" >> ../conf/messages.cy
echo "doYouNeedToStopRO.Yes = lawn" >> ../conf/messages.cy
echo "doYouNeedToStopRO.No = Na, mae angen i fi ddadgofrestru o’r cynllun Ymholiadau Olew Ad-daliedig" >> ../conf/messages.cy
echo "doYouNeedToStopRO.error.required = Dewiswch Iawn os oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Olew Ad-daliedig" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopRONextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopRO completed"
