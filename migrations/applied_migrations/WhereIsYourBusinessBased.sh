#!/bin/bash

echo "Applying migration WhereIsYourBusinessBased"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/moss                  controllers.vat.moss.WhereIsYourBusinessBasedController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss                  controllers.vat.moss.WhereIsYourBusinessBasedController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WhereIsYourBusinessBased" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "whereIsYourBusinessBased.title = Where is your business based?" >> ../conf/messages.en
echo "whereIsYourBusinessBased.heading = Where is your business based?" >> ../conf/messages.en
echo "whereIsYourBusinessBased.option1 = whereIsYourBusinessBased" Option 1 >> ../conf/messages.en
echo "whereIsYourBusinessBased.option2 = whereIsYourBusinessBased" Option 2 >> ../conf/messages.en
echo "whereIsYourBusinessBased.error.required = Select a location" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WhereIsYourBusinessBased" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "whereIsYourBusinessBased.title = Ymhle mae eich busnes wedi'i leoli?" >> ../conf/messages.cy
echo "whereIsYourBusinessBased.heading = Ymhle mae eich busnes wedi'i leoli?" >> ../conf/messages.cy
echo "whereIsYourBusinessBased.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whereIsYourBusinessBased.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whereIsYourBusinessBased.error.required = Dewis lleoliad" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.moss.WhereIsYourBusinessBasedNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhereIsYourBusinessBased completed"
