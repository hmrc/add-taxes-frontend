#!/bin/bash

echo "Applying migration DoYouNeedToStopPSA"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /psa/how-to-stop-psa                  controllers.deenrolment.DoYouNeedToStopPSAController.onPageLoad()" >> ../conf/app.routes
echo "POST       /psa/how-to-stop-psa                  controllers.deenrolment.DoYouNeedToStopPSAController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopPSA" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopPSA.title = Do you need to stop acting as a pension scheme administrator?" >> ../conf/messages.en
echo "doYouNeedToStopPSA.heading = Do you need to stop acting as a pension scheme administrator?" >> ../conf/messages.en
echo "doYouNeedToStopPSA.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopPSA.No = No, I need to stop using the online service" >> ../conf/messages.en
echo "doYouNeedToStopPSA.error.required = Select yes if you need to stop acting as a pension scheme administrator" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopPSA" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopPSA.title = A oes angen i chi roi’r gorau i weithredu fel gweinyddwr cynllun pensiwn?" >> ../conf/messages.cy
echo "doYouNeedToStopPSA.heading = A oes angen i chi roi’r gorau i weithredu fel gweinyddwr cynllun pensiwn?" >> ../conf/messages.cy
echo "doYouNeedToStopPSA.Yes = lawn" >> ../conf/messages.cy
echo "doYouNeedToStopPSA.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein" >> ../conf/messages.cy
echo "doYouNeedToStopPSA.error.required = Dewiswch Iawn os oes angen i chi roi’r gorau i weithredu fel gweinyddwr cynllun pensiwn" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopPSANextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopPSA completed"
