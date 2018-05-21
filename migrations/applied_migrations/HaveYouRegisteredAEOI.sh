#!/bin/bash

echo "Applying migration HaveYouRegisteredAEOI"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/aeoi                  controllers.other.aeoi.HaveYouRegisteredAEOIController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/aeoi                  controllers.other.aeoi.HaveYouRegisteredAEOIController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  HaveYouRegisteredAEOI" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "haveYouRegisteredAEOI.title = Have you already registered for Automatic Exchange of Information (AEOI)?" >> ../conf/messages.en
echo "haveYouRegisteredAEOI.heading = Have you already registered for Automatic Exchange of Information (AEOI)?" >> ../conf/messages.en
echo "haveYouRegisteredAEOI.Yes = Yes - I have an ID" >> ../conf/messages.en
echo "haveYouRegisteredAEOI.No = No - I haven't registered yet" >> ../conf/messages.en
echo "haveYouRegisteredAEOI.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  HaveYouRegisteredAEOI" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "haveYouRegisteredAEOI.title = A ydych eisoes wedi cofrestru ar gyfer Cyfnewid Gwybodaeth yn Awtomatig (AEOI)?" >> ../conf/messages.cy
echo "haveYouRegisteredAEOI.heading = A ydych eisoes wedi cofrestru ar gyfer Cyfnewid Gwybodaeth yn Awtomatig (AEOI)?" >> ../conf/messages.cy
echo "haveYouRegisteredAEOI.Yes = Ydw - mae gennyf ID" >> ../conf/messages.cy
echo "haveYouRegisteredAEOI.No = Nac ydw - nid wyf wedi cofrestru ar hyn o bryd" >> ../conf/messages.cy
echo "haveYouRegisteredAEOI.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val haveYouRegisteredAEOI: NextPage[HaveYouRegisteredAEOIId.type,";\
     print "    models.other.aeoi.HaveYouRegisteredAEOI] = {";\
     print "    new NextPage[HaveYouRegisteredAEOIId.type, models.other.aeoi.HaveYouRegisteredAEOI] {";\
     print "      override def get(b: models.other.aeoi.HaveYouRegisteredAEOI)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.aeoi.HaveYouRegisteredAEOI.Yes => ???";\
     print "          case models.other.aeoi.HaveYouRegisteredAEOI.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouRegisteredAEOI completed"
