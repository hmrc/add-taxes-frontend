#!/bin/bash

echo "Applying migration FindingYourAccount"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /findingYourAccount               controllers.wrongcredentials.FindingYourAccountController.onPageLoad()" >> ../conf/app.routes
echo "POST       /findingYourAccount               controllers.wrongcredentials.FindingYourAccountController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  FindingYourAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "findingYourAccount.title = findingYourAccount" >> ../conf/messages.en
echo "findingYourAccount.heading = findingYourAccount" >> ../conf/messages.en
echo "findingYourAccount.option1 = findingYourAccount" Option 1 >> ../conf/messages.en
echo "findingYourAccount.option2 = findingYourAccount" Option 2 >> ../conf/messages.en
echo "findingYourAccount.checkYourAnswersLabel = findingYourAccount" >> ../conf/messages.en
echo "findingYourAccount.error.required = Please give an answer for findingYourAccount" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  FindingYourAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "findingYourAccount.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "findingYourAccount.heading = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "findingYourAccount.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "findingYourAccount.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "findingYourAccount.checkYourAnswersLabel = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "findingYourAccount.error.required = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val findingYourAccount: NextPage[FindingYourAccountId.type,";\
     print "    FindingYourAccount] = {";\
     print "    new NextPage[FindingYourAccountId.type, FindingYourAccount] {";\
     print "      override def get(b: FindingYourAccount)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.wrongcredentials.FindingYourAccount.Option1 => routes.IndexController.onPageLoad()";\
     print "          case models.wrongcredentials.FindingYourAccount.Option2 => routes.IndexController.onPageLoad()";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration FindingYourAccount completed"
