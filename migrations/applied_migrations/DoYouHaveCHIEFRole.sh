#!/bin/bash

echo "Applying migration DoYouHaveCHIEFRole"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/import-export/nes/has-eori                  controllers.other.importexports.nes.DoYouHaveCHIEFRoleController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/import-export/nes/has-eori                  controllers.other.importexports.nes.DoYouHaveCHIEFRoleController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveCHIEFRole" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveCHIEFRole.title = Do you have a CHIEF user role?" >> ../conf/messages.en
echo "doYouHaveCHIEFRole.heading = Do you have a CHIEF user role?" >> ../conf/messages.en
echo "doYouHaveCHIEFRole.Yes = Yes - I have a CHIEF role" >> ../conf/messages.en
echo "doYouHaveCHIEFRole.No = No - I don't have a CHIEF role yet" >> ../conf/messages.en
echo "doYouHaveCHIEFRole.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveCHIEFRole" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveCHIEFRole.title = A oes gennych rôl defnyddiwr CHIEF?" >> ../conf/messages.cy
echo "doYouHaveCHIEFRole.heading = A oes gennych rôl defnyddiwr CHIEF?" >> ../conf/messages.cy
echo "doYouHaveCHIEFRole.Yes = Oes - mae gennyf rôl CHIEF" >> ../conf/messages.cy
echo "doYouHaveCHIEFRole.No = Nac oes - nid oes gennyf rôl CHIEF ar hyn o bryd" >> ../conf/messages.cy
echo "doYouHaveCHIEFRole.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouHaveCHIEFRole: NextPage[DoYouHaveCHIEFRoleId.type,";\
     print "    models.other.importexports.nes.DoYouHaveCHIEFRole] = {";\
     print "    new NextPage[DoYouHaveCHIEFRoleId.type, models.other.importexports.nes.DoYouHaveCHIEFRole] {";\
     print "      override def get(b: models.other.importexports.nes.DoYouHaveCHIEFRole)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.importexports.nes.DoYouHaveCHIEFRole.Yes => ???";\
     print "          case models.other.importexports.nes.DoYouHaveCHIEFRole.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveCHIEFRole completed"
