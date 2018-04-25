#!/bin/bash

echo "Applying migration RegisterEORI"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /registerEORI                       controllers.other.importexports.ics.RegisterEORIController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterEORI" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerEORI.title = registerEORI" >> ../conf/messages.en
echo "registerEORI.heading = registerEORI" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterEORI" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerEORI.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "registerEORI.heading = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterEORI completed"
