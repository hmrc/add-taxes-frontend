#!/bin/bash

echo "Applying migration AddEmployersPAYE"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/ers/epaye/not-enrolled                       controllers.employer.ers.AddEmployersPAYEController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AddEmployersPAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "addEmployersPAYE.title = Add PAYE for employers first" >> ../conf/messages.en
echo "addEmployersPAYE.heading = Add PAYE for employers first" >> ../conf/messages.en
echo "addEmployersPAYE.continue = Add PAYE for employers" >> ../conf/messages.en
echo "addEmployersPAYE.notnow = I don't want to do this right now" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AddEmployersPAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "addEmployersPAYE.title = Ychwanegwch TWE i gyflogwyr yn gyntaf" >> ../conf/messages.cy
echo "addEmployersPAYE.heading = Ychwanegwch TWE i gyflogwyr yn gyntaf" >> ../conf/messages.cy
echo "addEmployersPAYE.continue = Ychwanegu TWE i gyflogwyr" >> ../conf/messages.cy
echo "addEmployersPAYE.notnow = Nid wyf eisiau gwneud hyn ar hyn o bryd" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AddEmployersPAYE completed"
