#!/bin/bash

echo "Applying migration RegisterGBD"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/gambling/gbd/register                       controllers.other.gambling.gbd.RegisterGBDController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterGBD" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerGBD.title = Register for the Gambling Tax System first" >> ../conf/messages.en
echo "registerGBD.heading = Register for the Gambling Tax System first" >> ../conf/messages.en
echo "registerGBD.continue = Continue - register for the Gambling Tax System" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterGBD" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerGBD.title = Cofrestrwch ar gyfer y System Treth Hapchwarae yn gyntaf" >> ../conf/messages.cy
echo "registerGBD.heading = Cofrestrwch ar gyfer y System Treth Hapchwarae yn gyntaf" >> ../conf/messages.cy
echo "registerGBD.continue = Continue - register for the Gambling Tax System" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterGBD completed"
