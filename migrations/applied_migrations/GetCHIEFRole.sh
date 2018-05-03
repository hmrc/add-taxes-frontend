#!/bin/bash

echo "Applying migration GetCHIEFRole"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/import-export/nes/has-eori/register-chief                       controllers.other.importexports.nes.GetCHIEFRoleController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  GetCHIEFRole" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "getCHIEFRole.title = Get a CHIEF role first" >> ../conf/messages.en
echo "getCHIEFRole.heading = Get a CHIEF role first" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  GetCHIEFRole" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "getCHIEFRole.title = Dylech gael rôl CHIEF yn gyntaf" >> ../conf/messages.cy
echo "getCHIEFRole.heading = Dylech gael rôl CHIEF yn gyntaf" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration GetCHIEFRole completed"
