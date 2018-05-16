#!/bin/bash

echo "Applying migration DoesBusinessManagePAYE"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis/uk/contractor/epaye                  controllers.employer.cis.uk.contractor.DoesBusinessManagePAYEController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis/uk/contractor/epaye                  controllers.employer.cis.uk.contractor.DoesBusinessManagePAYEController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoesBusinessManagePAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doesBusinessManagePAYE.title = Does your business manage PAYE for employers online?" >> ../conf/messages.en
echo "doesBusinessManagePAYE.heading = Does your business manage PAYE for employers online?" >> ../conf/messages.en
echo "doesBusinessManagePAYE.Yes = Yes - we manage PAYE for employers online" >> ../conf/messages.en
echo "doesBusinessManagePAYE.No = No - we don't manage PAYE for employers online" >> ../conf/messages.en
echo "doesBusinessManagePAYE.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoesBusinessManagePAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doesBusinessManagePAYE.title = A yw eich busnes yn rheoli TWE i gyflogwyr ar-lein?" >> ../conf/messages.cy
echo "doesBusinessManagePAYE.heading = A yw eich busnes yn rheoli TWE i gyflogwyr ar-lein?" >> ../conf/messages.cy
echo "doesBusinessManagePAYE.Yes = Ydym - rydym yn rheoli TWE i gyflogwyr ar-lein" >> ../conf/messages.cy
echo "doesBusinessManagePAYE.No = Nac ydym - nid ydym yn rheoli TWE i gyflogwyr ar-lein" >> ../conf/messages.cy
echo "doesBusinessManagePAYE.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doesBusinessManagePAYE: NextPage[DoesBusinessManagePAYEId.type,";\
     print "    models.employer.cis.uk.contractor.DoesBusinessManagePAYE] = {";\
     print "    new NextPage[DoesBusinessManagePAYEId.type, models.employer.cis.uk.contractor.DoesBusinessManagePAYE] {";\
     print "      override def get(b: models.employer.cis.uk.contractor.DoesBusinessManagePAYE)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.employer.cis.uk.contractor.DoesBusinessManagePAYE.Yes => ???";\
     print "          case models.employer.cis.uk.contractor.DoesBusinessManagePAYE.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoesBusinessManagePAYE completed"
