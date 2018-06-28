#!/bin/bash

echo "Applying migration DeenrolmentProxy"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /deenrol/:serviceName                       controllers.deenrolment.DeenrolmentProxyController.onPageLoad()" >> ../conf/app.routes

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DeenrolmentProxy completed"
