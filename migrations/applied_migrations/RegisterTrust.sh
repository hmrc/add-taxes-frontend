#!/bin/bash

echo "Applying migration RegisterTrust"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /self-assessment/trust/not-registered                       controllers.sa.trust.RegisterTrustController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterTrust" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerTrust.title = Register your trust" >> ../conf/messages.en
echo "registerTrust.heading = Register your trust" >> ../conf/messages.en
echo "registerTrust.continue = Register your trust" >> ../conf/messages.en
echo "registerTrust.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterTrust" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerTrust.title = Cofrestru eich ymddiriedolaeth" >> ../conf/messages.cy
echo "registerTrust.heading = Cofrestru eich ymddiriedolaeth" >> ../conf/messages.cy
echo "registerTrust.continue = Cofrestru eich ymddiriedolaeth" >> ../conf/messages.cy
echo "registerTrust.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterTrust completed"
