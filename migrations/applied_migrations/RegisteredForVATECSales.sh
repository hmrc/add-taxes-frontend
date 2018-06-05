#!/bin/bash

echo "Applying migration RegisteredForVATECSales"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/ec                  controllers.vat.ec.RegisteredForVATECSalesController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/ec                  controllers.vat.ec.RegisteredForVATECSalesController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisteredForVATECSales" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registeredForVATECSales.title = Is the business registered for VAT?" >> ../conf/messages.en
echo "registeredForVATECSales.heading = Is the business registered for VAT?" >> ../conf/messages.en
echo "registeredForVATECSales.Yes = Yes" >> ../conf/messages.en
echo "registeredForVATECSales.No = No" >> ../conf/messages.en
echo "registeredForVATECSales.error.required = Select yes if the business is registered for VAT" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisteredForVATECSales" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registeredForVATECSales.title = A yw'r busnes wedi'i gofrestru ar gyfer TAW?" >> ../conf/messages.cy
echo "registeredForVATECSales.heading = A yw'r busnes wedi'i gofrestru ar gyfer TAW?" >> ../conf/messages.cy
echo "registeredForVATECSales.Yes = Iawn" >> ../conf/messages.cy
echo "registeredForVATECSales.No = Na" >> ../conf/messages.cy
echo "registeredForVATECSales.error.required = Dewiswch Iawn os yw’r busnes wedi'i gofrestru ar gyfer TAW" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.ec.RegisteredForVATECSalesNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredForVATECSales completed"
