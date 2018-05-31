#!/bin/bash

echo "Applying migration RegisterInHomeCountry"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/moss/eu                       controllers.vat.moss.eu.RegisterInHomeCountryController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterInHomeCountry" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerInHomeCountry.title = Register in your home country" >> ../conf/messages.en
echo "registerInHomeCountry.heading = Register in your home country" >> ../conf/messages.en
echo "registerInHomeCountry.continue = Continue" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterInHomeCountry" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerInHomeCountry.title = Cofrestru yn eich gwlad wreiddiol" >> ../conf/messages.cy
echo "registerInHomeCountry.heading = Cofrestru yn eich gwlad wreiddiol" >> ../conf/messages.cy
echo "registerInHomeCountry.continue = Parhau" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterInHomeCountry completed"
