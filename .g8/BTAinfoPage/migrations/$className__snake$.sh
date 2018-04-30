#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        $url$                       controllers.$package$.$className$Controller.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  $className$" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "$className;format="decap"$.title = $pageTitle$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $pageHeading$" >> ../conf/messages.en
echo "$className;format="decap"$.continue = $continueButton$" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  $className$" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "$className;format="decap"$.title = $welshPageTitle$" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = $welshPageHeading$" >> ../conf/messages.cy
echo "$className;format="decap"$.continue = $welshContinueButton$" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
