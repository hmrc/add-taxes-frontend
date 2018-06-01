#!/bin/bash

echo "Applying migration RegisterEmployersPAYE"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/ers/register-epaye                       controllers.employer.ers.RegisterEmployersPAYEController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterEmployersPAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerEmployersPAYE.title = Register for PAYE for employers first" >> ../conf/messages.en
echo "registerEmployersPAYE.heading = Register for PAYE for employers first" >> ../conf/messages.en
echo "registerEmployersPAYE.continue = Register for PAYE for employers" >> ../conf/messages.en
echo "registerEmployersPAYE.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterEmployersPAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerEmployersPAYE.title = Cofrestrwch ar gyfer TWE i gyflogwyr yn gyntaf" >> ../conf/messages.cy
echo "registerEmployersPAYE.heading = Cofrestrwch ar gyfer TWE i gyflogwyr yn gyntaf" >> ../conf/messages.cy
echo "registerEmployersPAYE.continue = Cofrestru ar gyfer TWE i gyflogwyr" >> ../conf/messages.cy
echo "registerEmployersPAYE.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterEmployersPAYE completed"
