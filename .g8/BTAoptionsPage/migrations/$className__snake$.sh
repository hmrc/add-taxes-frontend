#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        $url$                  controllers.$package$.$className$Controller.onPageLoad()" >> ../conf/app.routes
echo "POST       $url$                  controllers.$package$.$className$Controller.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  $className$" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "$className;format="decap"$.title = $pageTitle$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $pageHeading$" >> ../conf/messages.en
echo "$className;format="decap"$.option1 = $className;format="decap"$" Option 1 >> ../conf/messages.en
echo "$className;format="decap"$.option2 = $className;format="decap"$" Option 2 >> ../conf/messages.en
echo "$className;format="decap"$.error.required = $errorMessage$" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  $className$" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "$className;format="decap"$.title = $welshPageTitle$" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = $welshPageHeading$" >> ../conf/messages.cy
echo "$className;format="decap"$.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.error.required = $welshErrorMessage$" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.$package$.$className$NextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
