#!/bin/bash

echo "Applying migration RegisterMGD"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        other/gambling/mgd/register                       controllers.other.gambling.mgd.register.RegisterMGDController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterMGD" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerMGD.title = Register for Machine Games Duty" >> ../conf/messages.en
echo "registerMGD.heading = Register for Machine Games Duty" >> ../conf/messages.en
echo "registerMGD.continue = Continue - register for Machine Games Duty" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterMGD" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerMGD.title = Cofrestru ar gyfer Toll Peiriannau Hapchwarae" >> ../conf/messages.cy
echo "registerMGD.heading = Cofrestru ar gyfer Toll Peiriannau Hapchwarae" >> ../conf/messages.cy
echo "registerMGD.continue = Mynd yn eich blaen - cofrestru ar gyfer Toll Peiriannau Hapchwarae" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterMGD completed"
