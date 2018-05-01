#!/bin/bash

echo "Applying migration GetEORINumber"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/import-export/ncts/register                       controllers.other.importexport.ncts.GetEORINumberController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  GetEORINumber" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "getEORINumber.title = Get an EORI number first" >> ../conf/messages.en
echo "getEORINumber.heading = Get an EORI number first" >> ../conf/messages.en
echo "getEORINumber.continue = Continue - get an EORI number" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  GetEORINumber" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "getEORINumber.title = Dylech gael rhif EORI yn gyntaf" >> ../conf/messages.cy
echo "getEORINumber.heading = Dylech gael rhif EORI yn gyntaf" >> ../conf/messages.cy
echo "getEORINumber.continue = Parhau" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration GetEORINumber completed"
