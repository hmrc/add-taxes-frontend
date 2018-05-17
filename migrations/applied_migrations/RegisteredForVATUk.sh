#!/bin/bash

echo "Applying migration RegisteredForVATUk"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/moss/uk                  controllers.vat.moss.uk.RegisteredForVATUkController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss/uk                  controllers.vat.moss.uk.RegisteredForVATUkController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisteredForVATUk" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registeredForVATUk.title = Is your business registered for VAT in the UK?" >> ../conf/messages.en
echo "registeredForVATUk.heading = Is your business registered for VAT in the UK?" >> ../conf/messages.en
echo "registeredForVATUk.Yes = Yes - the business is registered for VAT in the UK" >> ../conf/messages.en
echo "registeredForVATUk.No = No - the business isn't VAT registered in the UK" >> ../conf/messages.en
echo "registeredForVATUk.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisteredForVATUk" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registeredForVATUk.title = A yw eich busnes wedi'i gofrestru ar gyfer TAW yn y DU?" >> ../conf/messages.cy
echo "registeredForVATUk.heading = A yw eich busnes wedi'i gofrestru ar gyfer TAW yn y DU?" >> ../conf/messages.cy
echo "registeredForVATUk.Yes = Ydy - mae'r busnes wedi'i gofrestru ar gyfer TAW yn y DU" >> ../conf/messages.cy
echo "registeredForVATUk.No = Nac ydy - nid yw'r busnes wedi'i gofrestru ar gyfer TAW yn y DU" >> ../conf/messages.cy
echo "registeredForVATUk.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val registeredForVATUk: NextPage[RegisteredForVATUkId.type,";\
     print "    models.vat.moss.uk.RegisteredForVATUk] = {";\
     print "    new NextPage[RegisteredForVATUkId.type, models.vat.moss.uk.RegisteredForVATUk] {";\
     print "      override def get(b: models.vat.moss.uk.RegisteredForVATUk)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.vat.moss.uk.RegisteredForVATUk.Yes => ???";\
     print "          case models.vat.moss.uk.RegisteredForVATUk.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredForVATUk completed"
