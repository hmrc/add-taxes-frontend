#!/bin/bash

echo "Applying migration DoYouHaveCharityReference"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/charities                  controllers.other.charity.DoYouHaveCharityReferenceController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/charities                  controllers.other.charity.DoYouHaveCharityReferenceController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveCharityReference" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveCharityReference.title = Do you have an HMRC Charities reference?" >> ../conf/messages.en
echo "doYouHaveCharityReference.heading = Do you have an HMRC Charities reference?" >> ../conf/messages.en
echo "doYouHaveCharityReference.Yes = Yes - we have an HMRC Charities reference" >> ../conf/messages.en
echo "doYouHaveCharityReference.No = No - we haven't got an HMRC charities reference yet" >> ../conf/messages.en
echo "doYouHaveCharityReference.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveCharityReference" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveCharityReference.title = A oes gennych gyfeirnod Elusennau CThEM?" >> ../conf/messages.cy
echo "doYouHaveCharityReference.heading = A oes gennych gyfeirnod Elusennau CThEM?" >> ../conf/messages.cy
echo "doYouHaveCharityReference.Yes = Oes - mae gennym gyfeirnod Elusennau CThEM" >> ../conf/messages.cy
echo "doYouHaveCharityReference.No = Nac oes - nid oes gennym gyfeirnod Elusennau CThEM ar hyn o bryd" >> ../conf/messages.cy
echo "doYouHaveCharityReference.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouHaveCharityReference: NextPage[DoYouHaveCharityReferenceId.type,";\
     print "    models.other.charity.DoYouHaveCharityReference] = {";\
     print "    new NextPage[DoYouHaveCharityReferenceId.type, models.other.charity.DoYouHaveCharityReference] {";\
     print "      override def get(b: models.other.charity.DoYouHaveCharityReference)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.other.charity.DoYouHaveCharityReference.Yes => ???";\
     print "          case models.other.charity.DoYouHaveCharityReference.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveCharityReference completed"
