#!/bin/bash

echo "Applying migration DoYouWantToAddPartner"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assessment/partnership                  controllers.sa.partnership.DoYouWantToAddPartnerController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assessment/partnership                  controllers.sa.partnership.DoYouWantToAddPartnerController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouWantToAddPartner" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouWantToAddPartner.title = Do you want to add a partner to an existing partnership?" >> ../conf/messages.en
echo "doYouWantToAddPartner.heading = Do you want to add a partner to an existing partnership?" >> ../conf/messages.en
echo "doYouWantToAddPartner.Yes = Yes - I want to add a partner to an existing partnership" >> ../conf/messages.en
echo "doYouWantToAddPartner.No = No - I want to do something else" >> ../conf/messages.en
echo "doYouWantToAddPartner.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouWantToAddPartner" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouWantToAddPartner.title = A ydych eisiau ychwanegu partner i bartneriaeth sy'n bodoli eisoes?" >> ../conf/messages.cy
echo "doYouWantToAddPartner.heading = A ydych eisiau ychwanegu partner i bartneriaeth sy'n bodoli eisoes?" >> ../conf/messages.cy
echo "doYouWantToAddPartner.Yes = Ydw - rwyf eisiau ychwanegu partner i bartneriaeth sy'n bodoli eisoes" >> ../conf/messages.cy
echo "doYouWantToAddPartner.No = Nac ydw - rwyf eisiau gwneud rhywbeth arall" >> ../conf/messages.cy
echo "doYouWantToAddPartner.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouWantToAddPartner: NextPage[DoYouWantToAddPartnerId.type,";\
     print "    models.sa.partnership.DoYouWantToAddPartner] = {";\
     print "    new NextPage[DoYouWantToAddPartnerId.type, models.sa.partnership.DoYouWantToAddPartner] {";\
     print "      override def get(b: models.sa.partnership.DoYouWantToAddPartner)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.sa.partnership.DoYouWantToAddPartner.Yes => ???";\
     print "          case models.sa.partnership.DoYouWantToAddPartner.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouWantToAddPartner completed"
