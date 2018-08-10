#!/bin/bash

echo "Applying migration EnterSAUTR"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /self-assessment/enter-sa-utr                       controllers.sa.EnterSAUTRController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  EnterSAUTR" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "enterSAUTR.title = Enter your Self Assessment Unique Taxpayer Reference (UTR)" >> ../conf/messages.en
echo "enterSAUTR.heading = Enter your Self Assessment Unique Taxpayer Reference (UTR)" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  EnterSAUTR" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "enterSAUTR.title = Nodwch eich Cyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Hunanasesiad" >> ../conf/messages.cy
echo "enterSAUTR.heading = Nodwch eich Cyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Hunanasesiad" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration EnterSAUTR completed"
