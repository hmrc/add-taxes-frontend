#!/bin/bash

echo "Applying migration GetEoriAndChiefRole"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/import-export/nes/no-eori/register-both                       controllers.other.importexports.nes.GetEoriAndChiefRoleController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  GetEoriAndChiefRole" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "getEoriAndChiefRole.title = Get an EORI number and CHIEF role" >> ../conf/messages.en
echo "getEoriAndChiefRole.heading = Get an EORI number and CHIEF role" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  GetEoriAndChiefRole" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "getEoriAndChiefRole.title = Dylech gael rhif EORI a rôl CHIEF" >> ../conf/messages.cy
echo "getEoriAndChiefRole.heading = Dylech gael rhif EORI a rôl CHIEF" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration GetEoriAndChiefRole completed"
