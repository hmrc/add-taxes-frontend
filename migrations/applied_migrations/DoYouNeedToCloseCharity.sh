#!/bin/bash

echo "Applying migration DoYouNeedToCloseCharity"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /charities/how-to-stop-charities                  controllers.deenrolment.DoYouNeedToCloseCharityController.onPageLoad()" >> ../conf/app.routes
echo "POST       /charities/how-to-stop-charities                  controllers.deenrolment.DoYouNeedToCloseCharityController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToCloseCharity" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToCloseCharity.title = Do you need to close the charity?" >> ../conf/messages.en
echo "doYouNeedToCloseCharity.heading = Do you need to close the charity?" >> ../conf/messages.en
echo "doYouNeedToCloseCharity.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToCloseCharity.No = No, I need to stop using the Charities online service" >> ../conf/messages.en
echo "doYouNeedToCloseCharity.error.required = Select yes if you need to close the charity" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToCloseCharity" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToCloseCharity.title = A oes angen i chi gau’r elusen?" >> ../conf/messages.cy
echo "doYouNeedToCloseCharity.heading = A oes angen i chi gau’r elusen?" >> ../conf/messages.cy
echo "doYouNeedToCloseCharity.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToCloseCharity.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth Elusennau ar-lein" >> ../conf/messages.cy
echo "doYouNeedToCloseCharity.error.required = Dewiswch Iawn os oes angen i chi gau’r elusen" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToCloseCharityNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToCloseCharity completed"
