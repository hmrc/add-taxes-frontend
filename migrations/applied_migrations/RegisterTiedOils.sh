#!/bin/bash

echo "Applying migration RegisterTiedOils"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /registerTiedOils                       controllers.RegisterTiedOilsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registerTiedOils.title = registerTiedOils" >> ../conf/messages.en
echo "registerTiedOils.heading = registerTiedOils" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterTiedOils completed"
