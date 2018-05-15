#!/bin/bash

echo "Applying migration DoYouHaveMGDRegistrationNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/gambling/mgd                  controllers.other.gambling.mgd.DoYouHaveMGDRegistrationNoController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/gambling/mgd                  controllers.other.gambling.mgd.DoYouHaveMGDRegistrationNoController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveMGDRegistrationNo" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveMGDRegistrationNo.title = Do you have a Machine Games Duty registration number?" >> ../conf/messages.en
echo "doYouHaveMGDRegistrationNo.heading = Do you have a Machine Games Duty registration number?" >> ../conf/messages.en
echo "doYouHaveMGDRegistrationNo.Yes = Yes - I have a Machine Games Duty registration number" >> ../conf/messages.en
echo "doYouHaveMGDRegistrationNo.No = No - I haven't registered yet" >> ../conf/messages.en
echo "doYouHaveMGDRegistrationNo.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveMGDRegistrationNo" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveMGDRegistrationNo.title = A oes gennych rif cofrestru Toll Peiriannau Hapchwarae?" >> ../conf/messages.cy
echo "doYouHaveMGDRegistrationNo.heading = A oes gennych rif cofrestru Toll Peiriannau Hapchwarae?" >> ../conf/messages.cy
echo "doYouHaveMGDRegistrationNo.Yes = Oes - mae gennyf rif cofrestru Toll Peiriannau Hapchwarae" >> ../conf/messages.cy
echo "doYouHaveMGDRegistrationNo.No = Nac ydw - nid wyf wedi cofrestru ar hyn o bryd" >> ../conf/messages.cy
echo "doYouHaveMGDRegistrationNo.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouHaveMGDRegistrationNo: NextPage[DoYouHaveMGDRegistrationNoId.type,";\
     print "    models.other.gambling.mgd.DoYouHaveMGDRegistrationNo] = {";\
     print "    new NextPage[DoYouHaveMGDRegistrationNoId.type, models.other.gambling.mgd.DoYouHaveMGDRegistrationNo] {";\
     print "      override def get(b: models.other.gambling.mgd.DoYouHaveMGDRegistrationNo)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.gambling.mgd.DoYouHaveMGDRegistrationNo.Yes => ???";\
     print "          case models.other.gambling.mgd.DoYouHaveMGDRegistrationNo.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveMGDRegistrationNo completed"
