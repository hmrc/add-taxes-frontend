#!/bin/bash

echo "Applying migration AreYouApprovedCTF"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/ctf/ctf-provider                  controllers.other.ctf.AreYouApprovedCTFController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/ctf/ctf-provider                  controllers.other.ctf.AreYouApprovedCTFController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AreYouApprovedCTF" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "areYouApprovedCTF.title = Are you an approved Child Trust Fund provider?" >> ../conf/messages.en
echo "areYouApprovedCTF.heading = Are you an approved Child Trust Fund provider?" >> ../conf/messages.en
echo "areYouApprovedCTF.Yes = Yes" >> ../conf/messages.en
echo "areYouApprovedCTF.No = No" >> ../conf/messages.en
echo "areYouApprovedCTF.error.required = Select yes if you are an approved Child Trust Fund provider" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AreYouApprovedCTF" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "areYouApprovedCTF.title = Ydych chi’n ddarparwr Cronfa Ymddiriedolaeth Plant cymeradwy?" >> ../conf/messages.cy
echo "areYouApprovedCTF.heading = Ydych chi’n ddarparwr Cronfa Ymddiriedolaeth Plant cymeradwy?" >> ../conf/messages.cy
echo "areYouApprovedCTF.Yes = Iawn" >> ../conf/messages.cy
echo "areYouApprovedCTF.No = Na" >> ../conf/messages.cy
echo "areYouApprovedCTF.error.required = Dewiswch ‘Iawn’ os ydych yn ddarparwr Cronfa Ymddiriedolaeth Plant cymeradwy" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.other.ctf.AreYouApprovedCTFNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AreYouApprovedCTF completed"
