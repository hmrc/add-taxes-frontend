#!/bin/bash

echo "Applying migration RegisterDefermentApprovalNumber"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/import-export/ddes/register                       controllers.other.importexports.dan.RegisterDefermentApprovalNumberController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterDefermentApprovalNumber" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerDefermentApprovalNumber.title = Get a Deferment Approval Number (DAN) first" >> ../conf/messages.en
echo "registerDefermentApprovalNumber.heading = Get a Deferment Approval Number (DAN) first" >> ../conf/messages.en
echo "registerDefermentApprovalNumber.continue = Continue - get a DAN" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterDefermentApprovalNumber" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerDefermentApprovalNumber.title = Dylech gael Rhif Cymeradwyo Gohirio (DAN) yn gyntaf" >> ../conf/messages.cy
echo "registerDefermentApprovalNumber.heading = Dylech gael Rhif Cymeradwyo Gohirio (DAN) yn gyntaf" >> ../conf/messages.cy
echo "registerDefermentApprovalNumber.continue = Mynd yn eich blaen - cael DAN" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterDefermentApprovalNumber completed"
