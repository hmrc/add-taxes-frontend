#!/bin/bash

echo "Applying migration UseEmployersPAYE"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/ers/epaye/other-account                       controllers.employer.ers.UseEmployersPAYEController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  UseEmployersPAYE" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "useEmployersPAYE.title = Use your PAYE for employers account" >> ../conf/messages.en
echo "useEmployersPAYE.heading = Use your PAYE for employers account" >> ../conf/messages.en
echo "useEmployersPAYE.continue = Sign in to your PAYE for employers account" >> ../conf/messages.en
echo "useEmployersPAYE.notnow = /business-account/sso-sign-out?continueUrl=%2Fbusiness-account" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  UseEmployersPAYE" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "useEmployersPAYE.title = Defnyddio eich cyfrif TWE i gyflogwyr" >> ../conf/messages.cy
echo "useEmployersPAYE.heading = Defnyddio eich cyfrif TWE i gyflogwyr" >> ../conf/messages.cy
echo "useEmployersPAYE.continue = Mewngofnodi i'ch cyfrif TWE i gyflogwyr" >> ../conf/messages.cy
echo "useEmployersPAYE.notnow = /business-account/sso-sign-out?continueUrl=%2Fbusiness-account" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UseEmployersPAYE completed"
