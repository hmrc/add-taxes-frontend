#!/bin/bash

echo "Applying migration RegisterEORI"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/import-export/ebti/register                       controllers.other.importexports.ebti.RegisterEORIController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterEORI" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerEORI.title = Get an EORI number first" >> ../conf/messages.en
echo "registerEORI.heading = Get an EORI number first" >> ../conf/messages.en
echo "registerEORI.continue = Continue - get an EORI number" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterEORI" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerEORI.title = Dylech gael rhif EORI yn gyntaf" >> ../conf/messages.cy
echo "registerEORI.heading = Dylech gael rhif EORI yn gyntaf" >> ../conf/messages.cy
echo "registerEORI.continue = Mynd yn eich blaen - cael rhif EORI" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterEORI completed"
