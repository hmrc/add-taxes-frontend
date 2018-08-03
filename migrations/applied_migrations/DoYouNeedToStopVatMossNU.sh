#!/bin/bash

echo "Applying migration DoYouNeedToStopVatMossNU"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/how-to-stop-vat-moss-nu                  controllers.deenrolment.DoYouNeedToStopVatMossNUController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/how-to-stop-vat-moss-nu                  controllers.deenrolment.DoYouNeedToStopVatMossNUController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToStopVatMossNU" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToStopVatMossNU.title = Do you need to leave this VAT MOSS scheme?" >> ../conf/messages.en
echo "doYouNeedToStopVatMossNU.heading = Do you need to leave this VAT MOSS scheme?" >> ../conf/messages.en
echo "doYouNeedToStopVatMossNU.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToStopVatMossNU.No = No, I need to stop using the online service" >> ../conf/messages.en
echo "doYouNeedToStopVatMossNU.error.required = Select yes if you need to leave this VAT MOSS scheme" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToStopVatMossNU" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToStopVatMossNU.title = A oes angen i chi adael y cynllun Gwasanaeth Un Cam ar gyfer TAW hwn?" >> ../conf/messages.cy
echo "doYouNeedToStopVatMossNU.heading = A oes angen i chi adael y cynllun Gwasanaeth Un Cam ar gyfer TAW hwn?" >> ../conf/messages.cy
echo "doYouNeedToStopVatMossNU.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToStopVatMossNU.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein" >> ../conf/messages.cy
echo "doYouNeedToStopVatMossNU.error.required = Dewiswch Iawn os oes angen i chi adael y cynllun Gwasanaeth Un Cam ar gyfer TAW hwn" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToStopVatMossNUNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToStopVatMossNU completed"
