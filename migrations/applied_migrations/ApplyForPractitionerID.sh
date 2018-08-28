#!/bin/bash

echo "Applying migration ApplyForPractitionerID"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/pension/practitioner/apply                       controllers.employer.pension.ApplyForPractitionerIDController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  ApplyForPractitionerID" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "applyForPractitionerID.title = You need a Scheme Practitioner ID to add this pension scheme to your account" >> ../conf/messages.en
echo "applyForPractitionerID.heading = You need a Scheme Practitioner ID to add this pension scheme to your account" >> ../conf/messages.en
echo "applyForPractitionerID.continue = Apply for a Scheme Practitioner ID" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  ApplyForPractitionerID" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "applyForPractitionerID.title = Mae angen Dynodydd (ID) Ymarferydd Cynllun arnoch er mwyn ychwanegu’r cynllun pensiwn hwn at eich cyfrif" >> ../conf/messages.cy
echo "applyForPractitionerID.heading = Mae angen Dynodydd (ID) Ymarferydd Cynllun arnoch er mwyn ychwanegu’r cynllun pensiwn hwn at eich cyfrif" >> ../conf/messages.cy
echo "applyForPractitionerID.continue = Gwneud cais am Ddynodydd (ID) Ymarferydd Cynllun" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration ApplyForPractitionerID completed"
