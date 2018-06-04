#!/bin/bash

echo "Applying migration RegisterForPAYE"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/intermediaries/register-epaye                       controllers.employer.intermediaries.RegisterForPAYEController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterForPAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerForPAYE.title = Register for PAYE for employers first" >> ../conf/messages.en
echo "registerForPAYE.heading = Register for PAYE for employers first" >> ../conf/messages.en
echo "registerForPAYE.continue = Register for PAYE for employers" >> ../conf/messages.en
echo "registerForPAYE.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterForPAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerForPAYE.title = Cofrestrwch ar gyfer TWE i gyflogwyr yn gyntaf" >> ../conf/messages.cy
echo "registerForPAYE.heading = Cofrestrwch ar gyfer TWE i gyflogwyr yn gyntaf" >> ../conf/messages.cy
echo "registerForPAYE.continue = Cofrestru ar gyfer TWE i gyflogwyr" >> ../conf/messages.cy
echo "registerForPAYE.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterForPAYE completed"
