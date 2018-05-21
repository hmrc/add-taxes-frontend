#!/bin/bash

echo "Applying migration RegisterAEOI"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/aeoi/register                       controllers.other.aeoi.RegisterAEOIController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterAEOI" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerAEOI.title = Register for Automatic Exchange of Information (AEOI)" >> ../conf/messages.en
echo "registerAEOI.heading = Register for Automatic Exchange of Information (AEOI)" >> ../conf/messages.en
echo "registerAEOI.continue = Continue - register for AEOI" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterAEOI" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerAEOI.title = Cofrestru ar gyfer Cyfnewid Gwybodaeth yn Awtomatig (AEOI)" >> ../conf/messages.cy
echo "registerAEOI.heading = Cofrestru ar gyfer Cyfnewid Gwybodaeth yn Awtomatig (AEOI)" >> ../conf/messages.cy
echo "registerAEOI.continue = Mynd yn eich blaen - cofrestru ar gyfer AEOI" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterAEOI completed"
