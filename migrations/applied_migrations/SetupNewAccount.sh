#!/bin/bash

echo "Applying migration SetupNewAccount"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/giant/newaccount                       controllers.vat.giant.SetupNewAccountController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SetupNewAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "setupNewAccount.title = Set up a new account" >> ../conf/messages.en
echo "setupNewAccount.heading = Set up a new account" >> ../conf/messages.en
echo "setupNewAccount.continue = Sign out and create ‘Organisation’ account" >> ../conf/messages.en
echo "setupNewAccount.notnow = I do not want to do this now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SetupNewAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "setupNewAccount.title = Sefydlu cyfrif newydd" >> ../conf/messages.cy
echo "setupNewAccount.heading = Sefydlu cyfrif newydd" >> ../conf/messages.cy
echo "setupNewAccount.continue = Allgofnodwch a chreu cyfrif ‘Sefydliad’" >> ../conf/messages.cy
echo "setupNewAccount.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SetupNewAccount completed"
