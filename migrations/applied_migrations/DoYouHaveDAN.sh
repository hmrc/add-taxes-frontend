#!/bin/bash

echo "Applying migration DoYouHaveDAN"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/import-export/ddes                  controllers.other.importexports.dan.DoYouHaveDANController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/import-export/ddes                  controllers.other.importexports.dan.DoYouHaveDANController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveDAN" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveDAN.title = Do you have a Deferment Approval Number (DAN)?" >> ../conf/messages.en
echo "doYouHaveDAN.heading = Do you have a Deferment Approval Number (DAN)?" >> ../conf/messages.en
echo "doYouHaveDAN.Yes = Yes - I have a Deferment Approval Number" >> ../conf/messages.en
echo "doYouHaveDAN.No = No - I don't have a DAN yet" >> ../conf/messages.en
echo "doYouHaveDAN.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveDAN" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveDAN.title = A oes gennych Rif Cymeradwyo Gohirio (DAN)?" >> ../conf/messages.cy
echo "doYouHaveDAN.heading = A oes gennych Rif Cymeradwyo Gohirio (DAN)?" >> ../conf/messages.cy
echo "doYouHaveDAN.Yes = Oes - mae gennyf Rif Cymeradwyo Gohirio" >> ../conf/messages.cy
echo "doYouHaveDAN.No = Nac oes - nid oes gennyf DAN ar hyn o bryd" >> ../conf/messages.cy
echo "doYouHaveDAN.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouHaveDAN: NextPage[DoYouHaveDANId.type,";\
     print "    models.other.importexports.dan.DoYouHaveDAN] = {";\
     print "    new NextPage[DoYouHaveDANId.type, models.other.importexports.dan.DoYouHaveDAN] {";\
     print "      override def get(b: models.other.importexports.dan.DoYouHaveDAN)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.importexports.dan.DoYouHaveDAN.Yes => ???";\
     print "          case models.other.importexports.dan.DoYouHaveDAN.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveDAN completed"
