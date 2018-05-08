#!/bin/bash

echo "Applying migration RegisterEORI"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        Continue - get an EORI number</a>/other/import-export/nes/no-eori/register-eori                       controllers.other.importexports.nes.RegisterEORIController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterEORI" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerEORI.title = MyPageTitle" >> ../conf/messages.en
echo "registerEORI.heading = MyPageHeading" >> ../conf/messages.en
echo "registerEORI.continue = Continue" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterEORI" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerEORI.title = WelshPageTitle" >> ../conf/messages.cy
echo "registerEORI.heading = WelshPageHeading" >> ../conf/messages.cy
echo "registerEORI.continue = Parhau" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterEORI completed"
