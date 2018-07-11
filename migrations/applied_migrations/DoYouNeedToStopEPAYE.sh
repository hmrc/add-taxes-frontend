#!/bin/bash

echo "Applying migration DoYouNeedToStopEPAYE"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /epaye/how-to-stop-paye                  controllers.deenrolment.DoYouNeedToStopEPAYEController.onPageLoad()" >> ../conf/app.routes
echo "POST       /epaye/how-to-stop-paye                  controllers.deenrolment.DoYouNeedToStopEPAYEController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopEPAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopEPAYE.title = Has your business stopped employing people?" >> ../conf/messages.en
echo "doYouNeedToStopEPAYE.heading = Has your business stopped employing people?" >> ../conf/messages.en
echo "doYouNeedToStopEPAYE.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopEPAYE.No = No, I need to stop using the PAYE for Employers online service" >> ../conf/messages.en
echo "doYouNeedToStopEPAYE.error.required = Select yes if your business has stopped employing people" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopEPAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopEPAYE.title = A yw’ch busnes wedi rhoi’r gorau i gyflogi pobl?" >> ../conf/messages.cy
echo "doYouNeedToStopEPAYE.heading = A yw’ch busnes wedi rhoi’r gorau i gyflogi pobl?" >> ../conf/messages.cy
echo "doYouNeedToStopEPAYE.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToStopEPAYE.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein TWE i gyflogwyr" >> ../conf/messages.cy
echo "doYouNeedToStopEPAYE.error.required = Dewiswch Iawn os yw’ch busnes wedi rhoi’r gorau i gyflogi pobl" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopEPAYENextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopEPAYE completed"
