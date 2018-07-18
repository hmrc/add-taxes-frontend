#!/bin/bash

echo "Applying migration DoYouNeedToCancelVAT"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/how-to-stop-vat                  controllers.deenrolment.DoYouNeedToCancelVATController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/how-to-stop-vat                  controllers.deenrolment.DoYouNeedToCancelVATController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouNeedToCancelVAT" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouNeedToCancelVAT.title = Do you need to cancel VAT registration and stop submitting returns?" >> ../conf/messages.en
echo "doYouNeedToCancelVAT.heading = Do you need to cancel VAT registration and stop submitting returns?" >> ../conf/messages.en
echo "doYouNeedToCancelVAT.Yes = Yes" >> ../conf/messages.en
echo "doYouNeedToCancelVAT.No = No, I need to stop using the VAT online service" >> ../conf/messages.en
echo "doYouNeedToCancelVAT.error.required = Select yes if you need to cancel VAT registration and stop submitting returns" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouNeedToCancelVAT" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouNeedToCancelVAT.title = A oes angen i chi ddileu cofrestriad TAW a rhoi’r gorau i gyflwyno Ffurflenni TAW?" >> ../conf/messages.cy
echo "doYouNeedToCancelVAT.heading = A oes angen i chi ddileu cofrestriad TAW a rhoi’r gorau i gyflwyno Ffurflenni TAW?" >> ../conf/messages.cy
echo "doYouNeedToCancelVAT.Yes = Iawn" >> ../conf/messages.cy
echo "doYouNeedToCancelVAT.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth TAW ar-lein" >> ../conf/messages.cy
echo "doYouNeedToCancelVAT.error.required = Dewiswch Iawn os oes angen i chi ddileu cofrestriad TAW a rhoi’r gorau i gyflwyno Ffurflenni TAW" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouNeedToCancelVATNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouNeedToCancelVAT completed"
