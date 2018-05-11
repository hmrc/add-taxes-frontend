#!/bin/bash

echo "Applying migration HaveYouRegisteredPartnership"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assessment/partnership/other                  controllers.sa.partnership.HaveYouRegisteredPartnershipController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assessment/partnership/other                  controllers.sa.partnership.HaveYouRegisteredPartnershipController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  HaveYouRegisteredPartnership" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "haveYouRegisteredPartnership.title = Have you already registered your partnership?" >> ../conf/messages.en
echo "haveYouRegisteredPartnership.heading = Have you already registered your partnership?" >> ../conf/messages.en
echo "haveYouRegisteredPartnership.Yes = Yes - I've registered the partnership and have a UTR" >> ../conf/messages.en
echo "haveYouRegisteredPartnership.No = No - I haven't registered the partnership" >> ../conf/messages.en
echo "haveYouRegisteredPartnership.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  HaveYouRegisteredPartnership" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "haveYouRegisteredPartnership.title = A ydych eisoes wedi cofrestru eich partneriaeth?" >> ../conf/messages.cy
echo "haveYouRegisteredPartnership.heading = A ydych eisoes wedi cofrestru eich partneriaeth?" >> ../conf/messages.cy
echo "haveYouRegisteredPartnership.Yes = Ydw - rwyf wedi cofrestru'r bartneriaeth ac mae gennyf UTR" >> ../conf/messages.cy
echo "haveYouRegisteredPartnership.No = Nac ydw - nid wyf wedi cofrestru'r bartneriaeth" >> ../conf/messages.cy
echo "haveYouRegisteredPartnership.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val haveYouRegisteredPartnership: NextPage[HaveYouRegisteredPartnershipId.type,";\
     print "    models.sa.partnership.HaveYouRegisteredPartnership] = {";\
     print "    new NextPage[HaveYouRegisteredPartnershipId.type, models.sa.partnership.HaveYouRegisteredPartnership] {";\
     print "      override def get(b: models.sa.partnership.HaveYouRegisteredPartnership)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.sa.partnership.HaveYouRegisteredPartnership.Yes => ???";\
     print "          case models.sa.partnership.HaveYouRegisteredPartnership.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouRegisteredPartnership completed"
