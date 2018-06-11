#!/bin/bash

echo "Applying migration SetUpANewAccount"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/moss/newaccount                       controllers.vat.moss.newaccount.SetUpANewAccountController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SetUpANewAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "setUpANewAccount.title = Set up a new account" >> ../conf/messages.en
echo "setUpANewAccount.heading = Set up a new account" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SetUpANewAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "setUpANewAccount.title = Sefydlu cyfrif newydd" >> ../conf/messages.cy
echo "setUpANewAccount.heading = Sefydlu cyfrif newydd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SetUpANewAccount completed"
