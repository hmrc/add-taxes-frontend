#!/bin/bash

echo "Applying migration RegisteredForVAT"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/moss/iom                  controllers.vat.moss.iom.RegisteredForVATController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss/iom                  controllers.vat.moss.iom.RegisteredForVATController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisteredForVAT" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registeredForVAT.title = Is your business registered for VAT on the Isle of Man?" >> ../conf/messages.en
echo "registeredForVAT.heading = Is your business registered for VAT on the Isle of Man?" >> ../conf/messages.en
echo "registeredForVAT.Yes = Yes - the business is registered for VAT on the Isle of Man?" >> ../conf/messages.en
echo "registeredForVAT.No = No - the business isn't VAT registered on the Isle of Man?" >> ../conf/messages.en
echo "registeredForVAT.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisteredForVAT" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registeredForVAT.title = A yw eich busnes wedi'i gofrestru ar gyfer TAW ar Ynys Manaw?" >> ../conf/messages.cy
echo "registeredForVAT.heading = A yw eich busnes wedi'i gofrestru ar gyfer TAW ar Ynys Manaw?" >> ../conf/messages.cy
echo "registeredForVAT.Yes = Ydy - mae'r busnes wedi'i gofrestru ar gyfer TAW ar Ynys Manaw" >> ../conf/messages.cy
echo "registeredForVAT.No = Nac ydy - nid yw'r busnes wedi'i gofrestru ar gyfer TAW ar Ynys Manaw" >> ../conf/messages.cy
echo "registeredForVAT.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.moss.iom.RegisteredForVATNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredForVAT completed"
