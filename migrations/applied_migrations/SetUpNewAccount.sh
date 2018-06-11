#!/bin/bash

echo "Applying migration SetUpNewAccount"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /self-assessment/partnership/new-account                       controllers.sa.partnership.SetUpNewAccountController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SetUpNewAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "setUpNewAccount.title = Set up a new account" >> ../conf/messages.en
echo "setUpNewAccount.heading = Set up a new account" >> ../conf/messages.en
echo "setUpNewAccount.continue = Sign out and create  ''Organisation'' account" >> ../conf/messages.en
echo "setUpNewAccount.notnow = I don’t want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SetUpNewAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "setUpNewAccount.title = Sefydlu cyfrif newydd" >> ../conf/messages.cy
echo "setUpNewAccount.heading = Sefydlu cyfrif newydd" >> ../conf/messages.cy
echo "setUpNewAccount.continue = Allgofnodwch a chreu cyfrif "Sefydliad"" >> ../conf/messages.cy
echo "setUpNewAccount.notnow = Allgofnodwch a chreu cyfrif "Sefydliad"" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SetUpNewAccount completed"
