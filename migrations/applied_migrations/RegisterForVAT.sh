#!/bin/bash

echo "Applying migration RegisterForVAT"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/moss/iom/not-vat-registered                       controllers.vat.moss.iom.RegisterForVATController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterForVAT" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerForVAT.title = Register for VAT" >> ../conf/messages.en
echo "registerForVAT.heading = Register for VAT" >> ../conf/messages.en
echo "registerForVAT.continue = Register for VAT" >> ../conf/messages.en
echo "registerForVAT.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterForVAT" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerForVAT.title = Cofrestru ar gyfer TAW" >> ../conf/messages.cy
echo "registerForVAT.heading = Cofrestru ar gyfer TAW" >> ../conf/messages.cy
echo "registerForVAT.continue = Cofrestru ar gyfer TAW" >> ../conf/messages.cy
echo "registerForVAT.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterForVAT completed"
