#!/bin/bash

echo "Applying migration AddVATFirst"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/moss/uk/vat-registered/no-other-account                       controllers.vat.moss.uk.AddVATFirstController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AddVATFirst" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "addVATFirst.title = Add VAT first" >> ../conf/messages.en
echo "addVATFirst.heading = Add VAT first" >> ../conf/messages.en
echo "addVATFirst.continue = Add VAT to this account" >> ../conf/messages.en
echo "addVATFirst.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AddVATFirst" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "addVATFirst.title = Ychwanegwch TAW yn gyntaf" >> ../conf/messages.cy
echo "addVATFirst.heading = Ychwanegwch TAW yn gyntaf" >> ../conf/messages.cy
echo "addVATFirst.continue = Ychwanegu TAW i'r cyfrif hwn" >> ../conf/messages.cy
echo "addVATFirst.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AddVATFirst completed"
