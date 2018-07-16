#!/bin/bash

echo "Applying migration YouDoNotNeedVAT"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/vat-giant/dont-need-service                       controllers.vat.giant.YouDoNotNeedVATController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  YouDoNotNeedVAT" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "youDoNotNeedVAT.title = You do not need VAT for Government Information and National Health Trusts (GIANT)" >> ../conf/messages.en
echo "youDoNotNeedVAT.heading = You do not need VAT for Government Information and National Health Trusts (GIANT)" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  YouDoNotNeedVAT" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "youDoNotNeedVAT.title = Does dim angen TAW ar gyfer Ymddiriedolaethau Gwybodaeth Llywodraeth ac Iechyd Cenedlaethol (GIANT)" >> ../conf/messages.cy
echo "youDoNotNeedVAT.heading = Does dim angen TAW ar gyfer Ymddiriedolaethau Gwybodaeth Llywodraeth ac Iechyd Cenedlaethol (GIANT)" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouDoNotNeedVAT completed"
