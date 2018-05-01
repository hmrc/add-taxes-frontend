#!/bin/bash

echo "Applying migration HaveAnEORINumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /business-account/add-tax/other/import-export/ncts                  controllers.other.importexports.ncts.HaveAnEORINumberController.onPageLoad()" >> ../conf/app.routes
echo "POST       /business-account/add-tax/other/import-export/ncts                  controllers.other.importexports.ncts.HaveAnEORINumberController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  HaveAnEORINumber" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "haveAnEORINumber.title = Do you have an Economic Operators Registration and Identification (EORI) number?" >> ../conf/messages.en
echo "haveAnEORINumber.heading = Do you have an Economic Operators Registration and Identification (EORI) number?" >> ../conf/messages.en
echo "haveAnEORINumber.Yes = Yes - I have an EORI number" >> ../conf/messages.en
echo "haveAnEORINumber.No = No - I don't have an EORI number" >> ../conf/messages.en
echo "haveAnEORINumber.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  HaveAnEORINumber" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "haveAnEORINumber.title = A oes gennych rif Cofrestru ac Adnabod Gweithredwr Economaidd (EORI)? - Cyfrif treth busnes - GOV.UK" >> ../conf/messages.cy
echo "haveAnEORINumber.heading = A oes gennych rif Cofrestru ac Adnabod Gweithredwr Economaidd (EORI)?" >> ../conf/messages.cy
echo "haveAnEORINumber.Yes = Nac oes - nid oes gennyf rif EORI" >> ../conf/messages.cy
echo "haveAnEORINumber.No = Nac oes - nid oes gennyf rif EORI" >> ../conf/messages.cy
echo "haveAnEORINumber.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val haveAnEORINumber: NextPage[HaveAnEORINumberId.type,";\
     print "    models.other.importexports.ncts.HaveAnEORINumber] = {";\
     print "    new NextPage[HaveAnEORINumberId.type, models.other.importexports.ncts.HaveAnEORINumber] {";\
     print "      override def get(b: models.other.importexports.ncts.HaveAnEORINumber)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.importexports.ncts.HaveAnEORINumber.Yes => ???";\
     print "          case models.other.importexports.ncts.HaveAnEORINumber.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveAnEORINumber completed"
