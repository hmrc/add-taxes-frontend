#!/bin/bash

echo "Applying migration OtherTaxes"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /otherTaxes               controllers.OtherTaxesController.onPageLoad()" >> ../conf/app.routes
echo "POST       /otherTaxes               controllers.OtherTaxesController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  OtherTaxes" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "otherTaxes.title = otherTaxes" >> ../conf/messages.en
echo "otherTaxes.heading = otherTaxes" >> ../conf/messages.en
echo "otherTaxes.option1 = otherTaxes" Option 1 >> ../conf/messages.en
echo "otherTaxes.option2 = otherTaxes" Option 2 >> ../conf/messages.en
echo "otherTaxes.checkYourAnswersLabel = otherTaxes" >> ../conf/messages.en
echo "otherTaxes.error.required = Please give an answer for otherTaxes" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  OtherTaxes" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "otherTaxes.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "otherTaxes.heading = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "otherTaxes.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "otherTaxes.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "otherTaxes.checkYourAnswersLabel = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "otherTaxes.error.required = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val otherTaxes: NextPage[OtherTaxesId.type,";\
     print "    OtherTaxes] = {";\
     print "    new NextPage[OtherTaxesId.type, OtherTaxes] {";\
     print "      override def get(b: OtherTaxes)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.OtherTaxes.Option1 => routes.IndexController.onPageLoad()";\
     print "          case models.OtherTaxes.Option2 => routes.IndexController.onPageLoad()";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration OtherTaxes completed"
