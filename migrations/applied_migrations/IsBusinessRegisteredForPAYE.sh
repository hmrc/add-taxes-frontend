#!/bin/bash

echo "Applying migration IsBusinessRegisteredForPAYE"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis/uk/contractor                  controllers.employer.cis.uk.contractor.IsBusinessRegisteredForPAYEController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis/uk/contractor                  controllers.employer.cis.uk.contractor.IsBusinessRegisteredForPAYEController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  IsBusinessRegisteredForPAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "isBusinessRegisteredForPAYE.title = Is your business registered for PAYE for employers?" >> ../conf/messages.en
echo "isBusinessRegisteredForPAYE.heading = Is your business registered for PAYE for employers?" >> ../conf/messages.en
echo "isBusinessRegisteredForPAYE.Yes = Yes - the business is registered for PAYE for employers" >> ../conf/messages.en
echo "isBusinessRegisteredForPAYE.No = No - the business isn't registered for PAYE for employers" >> ../conf/messages.en
echo "isBusinessRegisteredForPAYE.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  IsBusinessRegisteredForPAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "isBusinessRegisteredForPAYE.title = A yw eich busnes wedi'i gofrestru ar gyfer TWE i gyflogwyr?" >> ../conf/messages.cy
echo "isBusinessRegisteredForPAYE.heading = A yw eich busnes wedi'i gofrestru ar gyfer TWE i gyflogwyr?" >> ../conf/messages.cy
echo "isBusinessRegisteredForPAYE.Yes = Ydy - mae'r busnes wedi'i gofrestru ar gyfer TWE i gyflogwyr" >> ../conf/messages.cy
echo "isBusinessRegisteredForPAYE.No = Nac ydy - nid yw'r busnes wedi'i gofrestru ar gyfer TWE i gyflogwyr" >> ../conf/messages.cy
echo "isBusinessRegisteredForPAYE.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val isBusinessRegisteredForPAYE: NextPage[IsBusinessRegisteredForPAYEId.type,";\
     print "    models.employer.cis.uk.contractor.IsBusinessRegisteredForPAYE] = {";\
     print "    new NextPage[IsBusinessRegisteredForPAYEId.type, models.employer.cis.uk.contractor.IsBusinessRegisteredForPAYE] {";\
     print "      override def get(b: models.employer.cis.uk.contractor.IsBusinessRegisteredForPAYE)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.employer.cis.uk.contractor.IsBusinessRegisteredForPAYE.Yes => ???";\
     print "          case models.employer.cis.uk.contractor.IsBusinessRegisteredForPAYE.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration IsBusinessRegisteredForPAYE completed"
