#!/bin/bash

echo "Applying migration SelectATax"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/land                  controllers.other.land.SelectATaxController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/land                  controllers.other.land.SelectATaxController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SelectATax" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "selectATax.title = Select a tax" >> ../conf/messages.en
echo "selectATax.heading = Select a tax" >> ../conf/messages.en
echo "selectATax.option1 = selectATax" Option 1 >> ../conf/messages.en
echo "selectATax.option2 = selectATax" Option 2 >> ../conf/messages.en
echo "selectATax.error.required = Select a tax" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SelectATax" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "selectATax.title = Dewis treth" >> ../conf/messages.cy
echo "selectATax.heading = Dewis treth" >> ../conf/messages.cy
echo "selectATax.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectATax.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectATax.error.required = Dewis treth" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.other.land.SelectATaxNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SelectATax completed"
