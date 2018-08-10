#!/bin/bash

echo "Applying migration DoYouHaveVATRegNumber"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/have-vrn                  controllers.vat.DoYouHaveVATRegNumberController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/have-vrn                  controllers.vat.DoYouHaveVATRegNumberController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveVATRegNumber" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveVATRegNumber.title = Do you have a VAT registration number?" >> ../conf/messages.en
echo "doYouHaveVATRegNumber.heading = Do you have a VAT registration number?" >> ../conf/messages.en
echo "doYouHaveVATRegNumber.Yes = Yes" >> ../conf/messages.en
echo "doYouHaveVATRegNumber.No = No" >> ../conf/messages.en
echo "doYouHaveVATRegNumber.error.required = Select yes if you have a VAT registration number" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveVATRegNumber" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveVATRegNumber.title = A oes gennych rif cofrestru TAW?" >> ../conf/messages.cy
echo "doYouHaveVATRegNumber.heading = A oes gennych rif cofrestru TAW?" >> ../conf/messages.cy
echo "doYouHaveVATRegNumber.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHaveVATRegNumber.No = Na" >> ../conf/messages.cy
echo "doYouHaveVATRegNumber.error.required = Dewiswch ‘Iawn’ os oes gennych rif cofrestru TAW" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.DoYouHaveVATRegNumberNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveVATRegNumber completed"
