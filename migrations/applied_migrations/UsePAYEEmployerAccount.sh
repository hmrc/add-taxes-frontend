#!/bin/bash

echo "Applying migration UsePAYEEmployerAccount"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/intermediaries/epaye/other-account                       controllers.employer.intermediaries.UsePAYEEmployerAccountController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  UsePAYEEmployerAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "usePAYEEmployerAccount.title = Use your PAYE for employers account" >> ../conf/messages.en
echo "usePAYEEmployerAccount.heading = Use your PAYE for employers account" >> ../conf/messages.en
echo "usePAYEEmployerAccount.continue = Sign in to your PAYE for employers account" >> ../conf/messages.en
echo "usePAYEEmployerAccount.notnow = I want to add intermediaries in this account" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  UsePAYEEmployerAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "usePAYEEmployerAccount.title = Defnyddio eich cyfrif TWE i gyflogwyr" >> ../conf/messages.cy
echo "usePAYEEmployerAccount.heading = Defnyddio eich cyfrif TWE i gyflogwyr" >> ../conf/messages.cy
echo "usePAYEEmployerAccount.continue = Mewngofnodi i'ch cyfrif TWE i gyflogwyr" >> ../conf/messages.cy
echo "usePAYEEmployerAccount.notnow = Rwyf eisiau ychwanegu cyfryngwyr yn y cyfrif hwn" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration UsePAYEEmployerAccount completed"
