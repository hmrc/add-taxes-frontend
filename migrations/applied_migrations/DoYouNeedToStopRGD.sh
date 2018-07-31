#!/bin/bash

echo "Applying migration DoYouNeedToStopRGD"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /gambling/how-to-stop-rgd                  controllers.deenrolment.DoYouNeedToStopRGDController.onPageLoad()" >> ../conf/app.routes
echo "POST       /gambling/how-to-stop-rgd                  controllers.deenrolment.DoYouNeedToStopRGDController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopRGD" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopRGD.title = Do you need to stop using the Remote Gaming Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopRGD.heading = Do you need to stop using the Remote Gaming Duty online service?" >> ../conf/messages.en
echo "doYouNeedToStopRGD.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopRGD.No = No, I need to deregister from Remote Gaming Duty" >> ../conf/messages.en
echo "doYouNeedToStopRGD.error.required = Select yes if you need to stop using the Remote Gaming Duty online service" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopRGD" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopRGD.title = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Hapchwarae o Bell?" >> ../conf/messages.cy
echo "doYouNeedToStopRGD.heading = A oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Hapchwarae o Bell?" >> ../conf/messages.cy
echo "doYouNeedToStopRGD.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToStopRGD.No = Na, mae angen i fi ddadgofrestru o’r Doll Hapchwarae o Bell" >> ../conf/messages.cy
echo "doYouNeedToStopRGD.error.required = Dewiswch Iawn os oes angen i chi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein Toll Hapchwarae o Bell" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopRGDNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopRGD completed"
