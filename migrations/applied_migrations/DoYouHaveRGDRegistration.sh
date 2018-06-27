#!/bin/bash

echo "Applying migration DoYouHaveRGDRegistration"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/gambling/rgd                  controllers.other.gambling.rgd.DoYouHaveRGDRegistrationController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/gambling/rgd                  controllers.other.gambling.rgd.DoYouHaveRGDRegistrationController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveRGDRegistration" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveRGDRegistration.title = Do you have a Remote Gaming Duty registration number?" >> ../conf/messages.en
echo "doYouHaveRGDRegistration.heading = Do you have a Remote Gaming Duty registration number?" >> ../conf/messages.en
echo "doYouHaveRGDRegistration.Yes = Yes" >> ../conf/messages.en
echo "doYouHaveRGDRegistration.No = No" >> ../conf/messages.en
echo "doYouHaveRGDRegistration.error.required = Select yes if you have a Remote Gaming Duty registration number" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveRGDRegistration" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveRGDRegistration.title = A oes gennych rif cofrestru Toll Hapchwarae o Bell?" >> ../conf/messages.cy
echo "doYouHaveRGDRegistration.heading = A oes gennych rif cofrestru Toll Hapchwarae o Bell?" >> ../conf/messages.cy
echo "doYouHaveRGDRegistration.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHaveRGDRegistration.No = Na" >> ../conf/messages.cy
echo "doYouHaveRGDRegistration.error.required = Dewiswch Iawn os oes gennych rif cofrestru ar gyfer Toll Hapchwarae o Bell" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.other.gambling.rgd.DoYouHaveRGDRegistrationNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveRGDRegistration completed"
