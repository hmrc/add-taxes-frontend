#!/bin/bash

echo "Applying migration EconomicOperatorsRegistrationAndIdentification"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /economicOperatorsRegistrationAndIdentification               controllers.EconomicOperatorsRegistrationAndIdentificationController.onPageLoad()" >> ../conf/app.routes
echo "POST       /economicOperatorsRegistrationAndIdentification               controllers.EconomicOperatorsRegistrationAndIdentificationController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  EconomicOperatorsRegistrationAndIdentification" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "economicOperatorsRegistrationAndIdentification.title = economicOperatorsRegistrationAndIdentification" >> ../conf/messages.en
echo "economicOperatorsRegistrationAndIdentification.heading = economicOperatorsRegistrationAndIdentification" >> ../conf/messages.en
echo "economicOperatorsRegistrationAndIdentification.option1 = economicOperatorsRegistrationAndIdentification" Option 1 >> ../conf/messages.en
echo "economicOperatorsRegistrationAndIdentification.option2 = economicOperatorsRegistrationAndIdentification" Option 2 >> ../conf/messages.en
echo "economicOperatorsRegistrationAndIdentification.checkYourAnswersLabel = economicOperatorsRegistrationAndIdentification" >> ../conf/messages.en
echo "economicOperatorsRegistrationAndIdentification.error.required = Please give an answer for economicOperatorsRegistrationAndIdentification" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  EconomicOperatorsRegistrationAndIdentification" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "economicOperatorsRegistrationAndIdentification.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "economicOperatorsRegistrationAndIdentification.heading = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "economicOperatorsRegistrationAndIdentification.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "economicOperatorsRegistrationAndIdentification.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "economicOperatorsRegistrationAndIdentification.checkYourAnswersLabel = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "economicOperatorsRegistrationAndIdentification.error.required = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val economicOperatorsRegistrationAndIdentification: NextPage[EconomicOperatorsRegistrationAndIdentificationId.type,";\
     print "    EconomicOperatorsRegistrationAndIdentification] = {";\
     print "    new NextPage[EconomicOperatorsRegistrationAndIdentificationId.type, EconomicOperatorsRegistrationAndIdentification] {";\
     print "      override def get(b: EconomicOperatorsRegistrationAndIdentification)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.EconomicOperatorsRegistrationAndIdentification.Option1 => routes.IndexController.onPageLoad()";\
     print "          case models.EconomicOperatorsRegistrationAndIdentification.Option2 => routes.IndexController.onPageLoad()";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration EconomicOperatorsRegistrationAndIdentification completed"
