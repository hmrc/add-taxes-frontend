#!/bin/bash

echo "Applying migration RegisterGTSFirst"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/gambling/pbd/register                       controllers.other.gambling.pbd.register.RegisterGTSFirstController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterGTSFirst" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerGTSFirst.title = Register for the Gambling Tax System first" >> ../conf/messages.en
echo "registerGTSFirst.heading = Register for the Gambling Tax System first" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterGTSFirst" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerGTSFirst.title = Cofrestrwch ar gyfer y System Treth Hapchwarae yn gyntaf" >> ../conf/messages.cy
echo "registerGTSFirst.heading = Cofrestrwch ar gyfer y System Treth Hapchwarae yn gyntaf" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterGTSFirst completed"
