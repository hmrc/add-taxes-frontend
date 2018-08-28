#!/bin/bash

echo "Applying migration RequestActivationToken"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/pension/need-activation-token                       controllers.employer.pension.RequestActivationTokenController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RequestActivationToken" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "requestActivationToken.title = Request access to Pension Schemes Online for Practitioners to get your activation token" >> ../conf/messages.en
echo "requestActivationToken.heading = Request access to Pension Schemes Online for Practitioners to get your activation token" >> ../conf/messages.en
echo "requestActivationToken.continue = Continue" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RequestActivationToken" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "requestActivationToken.title = Gofyn am gael cyrchu Cynlluniau Pensiwn Ar-lein ar gyfer Ymarferwyr i gael eich tocyn cychwyn" >> ../conf/messages.cy
echo "requestActivationToken.heading = Gofyn am gael cyrchu Cynlluniau Pensiwn Ar-lein ar gyfer Ymarferwyr i gael eich tocyn cychwyn" >> ../conf/messages.cy
echo "requestActivationToken.continue = Parhau" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RequestActivationToken completed"
