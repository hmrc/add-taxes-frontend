#!/bin/bash

echo "Applying migration DoYouHaveSAUTR"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assessment/have-sa-utr                  controllers.sa.DoYouHaveSAUTRController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assessment/have-sa-utr                  controllers.sa.DoYouHaveSAUTRController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveSAUTR" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveSAUTR.title = Do you have a Self Assessment Unique Taxpayer Reference (UTR)?" >> ../conf/messages.en
echo "doYouHaveSAUTR.heading = Do you have a Self Assessment Unique Taxpayer Reference (UTR)?" >> ../conf/messages.en
echo "doYouHaveSAUTR.Yes = Yes" >> ../conf/messages.en
echo "doYouHaveSAUTR.No = No" >> ../conf/messages.en
echo "doYouHaveSAUTR.error.required = Select yes if you have a Self Assessment Unique Taxpayer Reference (UTR)" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveSAUTR" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveSAUTR.title = A oes gennych Gyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Hunanasesiad?" >> ../conf/messages.cy
echo "doYouHaveSAUTR.heading = A oes gennych Gyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Hunanasesiad?" >> ../conf/messages.cy
echo "doYouHaveSAUTR.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHaveSAUTR.No = Na" >> ../conf/messages.cy
echo "doYouHaveSAUTR.error.required = Dewiswch ‘Iawn’ os oes gennych Gyfeirnod Unigryw y Trethdalwr ar gyfer Hunanasesiad" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.sa.DoYouHaveSAUTRNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveSAUTR completed"
