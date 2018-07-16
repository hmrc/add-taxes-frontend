#!/bin/bash

echo "Applying migration YouNeedToBeApprovedCTF"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/ctf/ctf-11                       controllers.other.ctf.YouNeedToBeApprovedCTFController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  YouNeedToBeApprovedCTF" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "youNeedToBeApprovedCTF.title = You need to be an approved Child Trust Fund provider to access this service" >> ../conf/messages.en
echo "youNeedToBeApprovedCTF.heading = You need to be an approved Child Trust Fund provider to access this service" >> ../conf/messages.en
echo "youNeedToBeApprovedCTF.continue = Apply to become an approved provider" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  YouNeedToBeApprovedCTF" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "youNeedToBeApprovedCTF.title = Mae angen i chi fod yn ddarparwr Cronfa Ymddiriedolaeth Plant cymeradwy i gyrchu’r gwasanaeth hwn" >> ../conf/messages.cy
echo "youNeedToBeApprovedCTF.heading = Mae angen i chi fod yn ddarparwr Cronfa Ymddiriedolaeth Plant cymeradwy i gyrchu’r gwasanaeth hwn" >> ../conf/messages.cy
echo "youNeedToBeApprovedCTF.continue = Gwneud cais i fod yn ddarparwr cymeradwy" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YouNeedToBeApprovedCTF completed"
