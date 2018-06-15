#!/bin/bash

echo "Applying migration StampDuty"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        other/land/stamp-duty                  controllers.other.land.stampduty.StampDutyController.onPageLoad()" >> ../conf/app.routes
echo "POST       other/land/stamp-duty                  controllers.other.land.stampduty.StampDutyController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  StampDuty" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "stampDuty.title = Is this service for your business?" >> ../conf/messages.en
echo "stampDuty.heading = Is this service for your business?" >> ../conf/messages.en
echo "stampDuty.Yes = Yes - this service is for business purposes" >> ../conf/messages.en
echo "stampDuty.No = No - I'm buying a property for myself" >> ../conf/messages.en
echo "stampDuty.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  StampDuty" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "stampDuty.title = A yw'r gwasanaeth hwn ar gyfer eich busnes?" >> ../conf/messages.cy
echo "stampDuty.heading = A yw'r gwasanaeth hwn ar gyfer eich busnes?" >> ../conf/messages.cy
echo "stampDuty.Yes = Ydy - mae'r gwasanaeth hwn at ddibenion busnes" >> ../conf/messages.cy
echo "stampDuty.No = Nac ydy - rwy'n prynu eiddo ar gyfer fy hun" >> ../conf/messages.cy
echo "stampDuty.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.other.land.stampduty.StampDutyNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration StampDuty completed"
