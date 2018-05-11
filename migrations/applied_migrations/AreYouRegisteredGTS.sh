#!/bin/bash

echo "Applying migration AreYouRegisteredGTS"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/gambling/gbd                  controllers.other.gambling.gbd.AreYouRegisteredGTSController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/gambling/gbd                  controllers.other.gambling.gbd.AreYouRegisteredGTSController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AreYouRegisteredGTS" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "areYouRegisteredGTS.title = Are you registered for the Gambling Tax System?" >> ../conf/messages.en
echo "areYouRegisteredGTS.heading = Are you registered for the Gambling Tax System?" >> ../conf/messages.en
echo "areYouRegisteredGTS.Yes = Yes - I have a Gambling Tax System reference number" >> ../conf/messages.en
echo "areYouRegisteredGTS.No = No - I haven't registered yet" >> ../conf/messages.en
echo "areYouRegisteredGTS.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AreYouRegisteredGTS" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "areYouRegisteredGTS.title = A ydych wedi cofrestru ar gyfer y System Treth Hapchwarae?" >> ../conf/messages.cy
echo "areYouRegisteredGTS.heading = A ydych wedi cofrestru ar gyfer y System Treth Hapchwarae?" >> ../conf/messages.cy
echo "areYouRegisteredGTS.Yes = Ydw - mae gennyf gyfeirnod System Treth Hapchwarae" >> ../conf/messages.cy
echo "areYouRegisteredGTS.No = Nac ydw - nid wyf wedi cofrestru ar hyn o bryd" >> ../conf/messages.cy
echo "areYouRegisteredGTS.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val areYouRegisteredGTS: NextPage[AreYouRegisteredGTSId.type,";\
     print "    models.other.gambling.gbd.AreYouRegisteredGTS] = {";\
     print "    new NextPage[AreYouRegisteredGTSId.type, models.other.gambling.gbd.AreYouRegisteredGTS] {";\
     print "      override def get(b: models.other.gambling.gbd.AreYouRegisteredGTS)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.gambling.gbd.AreYouRegisteredGTS.Yes => ???";\
     print "          case models.other.gambling.gbd.AreYouRegisteredGTS.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AreYouRegisteredGTS completed"
