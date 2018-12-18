#!/bin/bash

echo "Applying migration YourSaIsNotInThisAccount"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assessmnet/not-in-account                  controllers.sa.YourSaIsNotInThisAccountController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assessmnet/not-in-account                  controllers.sa.YourSaIsNotInThisAccountController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  YourSaIsNotInThisAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "yourSaIsNotInThisAccount.title = Your Self Assessment is not in this account" >> ../conf/messages.en
echo "yourSaIsNotInThisAccount.heading = Your Self Assessment is not in this account" >> ../conf/messages.en
echo "yourSaIsNotInThisAccount.option1 = yourSaIsNotInThisAccount" Option 1 >> ../conf/messages.en
echo "yourSaIsNotInThisAccount.option2 = yourSaIsNotInThisAccount" Option 2 >> ../conf/messages.en
echo "yourSaIsNotInThisAccount.error.required = There is a problem" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  YourSaIsNotInThisAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "yourSaIsNotInThisAccount.title = TRANSLATION NEEDED - Your Self Assessment is not in this account" >> ../conf/messages.cy
echo "yourSaIsNotInThisAccount.heading = TRANSLATION NEEDED - Your Self Assessment is not in this account" >> ../conf/messages.cy
echo "yourSaIsNotInThisAccount.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "yourSaIsNotInThisAccount.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "yourSaIsNotInThisAccount.error.required = TRANSLATION NEEDED - There is a problem" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.sa.YourSaIsNotInThisAccountNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourSaIsNotInThisAccount completed"
