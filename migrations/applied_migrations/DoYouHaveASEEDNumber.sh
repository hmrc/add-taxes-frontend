#!/bin/bash

echo "Applying migration DoYouHaveASEEDNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /doYouHaveASEEDNumber               controllers.other.importexports.emcs.DoYouHaveASEEDNumberController.onPageLoad()" >> ../conf/app.routes
echo "POST       /doYouHaveASEEDNumber               controllers.other.importexports.emcs.DoYouHaveASEEDNumberController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveASEEDNumber" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveASEEDNumber.title = doYouHaveASEEDNumber" >> ../conf/messages.en
echo "doYouHaveASEEDNumber.heading = doYouHaveASEEDNumber" >> ../conf/messages.en
echo "doYouHaveASEEDNumber.option1 = doYouHaveASEEDNumber" Option 1 >> ../conf/messages.en
echo "doYouHaveASEEDNumber.option2 = doYouHaveASEEDNumber" Option 2 >> ../conf/messages.en
echo "doYouHaveASEEDNumber.checkYourAnswersLabel = doYouHaveASEEDNumber" >> ../conf/messages.en
echo "doYouHaveASEEDNumber.error.required = Please give an answer for doYouHaveASEEDNumber" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveASEEDNumber" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveASEEDNumber.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouHaveASEEDNumber.heading = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouHaveASEEDNumber.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouHaveASEEDNumber.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouHaveASEEDNumber.checkYourAnswersLabel = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouHaveASEEDNumber.error.required = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouHaveASEEDNumber: NextPage[DoYouHaveASEEDNumberId.type,";\
     print "    DoYouHaveASEEDNumber] = {";\
     print "    new NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber] {";\
     print "      override def get(b: DoYouHaveASEEDNumber)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.DoYouHaveASEEDNumber.Option1 => routes.IndexController.onPageLoad()";\
     print "          case models.DoYouHaveASEEDNumber.Option2 => routes.IndexController.onPageLoad()";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveASEEDNumber completed"
