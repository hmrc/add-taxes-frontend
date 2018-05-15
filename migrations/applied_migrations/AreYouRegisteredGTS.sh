#!/bin/bash

echo "Applying migration AreYouRegisteredGTS"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/gambling/rgd                       controllers.other.gambling.rgd.AreYouRegisteredGTSController.onPageLoad()" >> ../conf/app.routes

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AreYouRegisteredGTS completed"
