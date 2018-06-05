#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        $url$                       controllers.$package$.$className$Controller.onPageLoad()" >> ../conf/app.routes
echo "POST       $url$                       controllers.$package$.$className$Controller.onSubmit()"   >> ../conf/app.routes

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.$package$.$className$NextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
