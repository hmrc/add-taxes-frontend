#!/bin/bash

echo "Applying migration RegisterRGD"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/gambling/rgd/register                       controllers.other.gambling.rgd.RegisterRGDController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterRGD" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerRGD.title = Register for the Gambling Tax System first" >> ../conf/messages.en
echo "registerRGD.heading = Register for the Gambling Tax System first" >> ../conf/messages.en
echo "registerRGD.continue = Continue - register for the Gambling Tax System" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterRGD" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerRGD.title = Cofrestrwch ar gyfer y System Treth Hapchwarae yn gyntaf" >> ../conf/messages.cy
echo "registerRGD.heading = Cofrestrwch ar gyfer y System Treth Hapchwarae yn gyntaf" >> ../conf/messages.cy
echo "registerRGD.continue = Mynd yn eich blaen - cofrestru ar gyfer y System Treth Hapchwarae" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterRGD completed"
