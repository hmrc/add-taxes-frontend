#!/bin/bash

echo "Applying migration SelectGamblingOrGamingDuty"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/gambling                  controllers.other.gambling.SelectGamblingOrGamingDutyController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/gambling                  controllers.other.gambling.SelectGamblingOrGamingDutyController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SelectGamblingOrGamingDuty" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "selectGamblingOrGamingDuty.title = Select a gambling or gaming duty" >> ../conf/messages.en
echo "selectGamblingOrGamingDuty.heading = Select a gambling or gaming duty" >> ../conf/messages.en
echo "selectGamblingOrGamingDuty.option1 = selectGamblingOrGamingDuty" Option 1 >> ../conf/messages.en
echo "selectGamblingOrGamingDuty.option2 = selectGamblingOrGamingDuty" Option 2 >> ../conf/messages.en
echo "selectGamblingOrGamingDuty.error.required = Select a duty" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SelectGamblingOrGamingDuty" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "selectGamblingOrGamingDuty.title = Dewiswch doll fetio neu hapchwarae" >> ../conf/messages.cy
echo "selectGamblingOrGamingDuty.heading = Dewiswch doll fetio neu hapchwarae" >> ../conf/messages.cy
echo "selectGamblingOrGamingDuty.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectGamblingOrGamingDuty.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectGamblingOrGamingDuty.error.required = Dewis toll" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val selectGamblingOrGamingDuty: NextPage[SelectGamblingOrGamingDutyId.type,";\
     print "    models.other.gambling.SelectGamblingOrGamingDuty] = {";\
     print "    new NextPage[SelectGamblingOrGamingDutyId.type, models.other.gambling.SelectGamblingOrGamingDuty] {";\
     print "      override def get(b: models.other.gambling.SelectGamblingOrGamingDuty)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.other.gambling.SelectGamblingOrGamingDuty.Option1 => ???";\
     print "          case models.other.gambling.SelectGamblingOrGamingDuty.Option2 => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SelectGamblingOrGamingDuty completed"
