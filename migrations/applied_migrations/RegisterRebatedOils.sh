#!/bin/bash

echo "Applying migration RegisterRebatedOils"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /registerRebatedOils                       controllers.other.oil.RegisterRebatedOilsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "registerRebatedOils.title = registerRebatedOils" >> ../conf/messages.en
echo "registerRebatedOils.heading = registerRebatedOils" >> ../conf/messages.en

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterRebatedOils completed"
