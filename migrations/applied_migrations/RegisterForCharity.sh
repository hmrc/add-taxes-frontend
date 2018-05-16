#!/bin/bash

echo "Applying migration RegisterForCharity"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/charities/register                       controllers.other.charity.RegisterForCharityController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterForCharity" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerForCharity.title = Register with HMRC as a Charity" >> ../conf/messages.en
echo "registerForCharity.heading = Register with HMRC as a Charity" >> ../conf/messages.en
echo "registerForCharity.continue = Continue - register with HMRC as a Charity" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterForCharity" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerForCharity.title = Cofrestru fel Elusen gyda CThEM" >> ../conf/messages.cy
echo "registerForCharity.heading = Cofrestru fel Elusen gyda CThEM" >> ../conf/messages.cy
echo "registerForCharity.continue = Mynd yn eich blaen - cofrestru fel Elusen gyda CThEM" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterForCharity completed"
