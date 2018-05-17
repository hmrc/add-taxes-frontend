#!/bin/bash

echo "Applying migration AddVATMOSS"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/moss/uk/vat-registered/other-account                       controllers.vat.moss.uk.AddVATMOSSController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AddVATMOSS" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "addVATMOSS.title = Add VAT MOSS to your VAT account" >> ../conf/messages.en
echo "addVATMOSS.heading = Add VAT MOSS to your VAT account" >> ../conf/messages.en
echo "addVATMOSS.continue = Sign in to your VAT account" >> ../conf/messages.en
echo "addVATMOSS.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AddVATMOSS" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "addVATMOSS.title = Ychwanegu GUC TAW i'ch cyfrif TAW" >> ../conf/messages.cy
echo "addVATMOSS.heading = Ychwanegu GUC TAW i'ch cyfrif TAW" >> ../conf/messages.cy
echo "addVATMOSS.continue = Mewngofnodi i'ch cyfrif TAW" >> ../conf/messages.cy
echo "addVATMOSS.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AddVATMOSS completed"
