#!/bin/bash

echo "Applying migration DoYouHavePractitionerID"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/pension/practitioner-ID                  controllers.employer.pension.DoYouHavePractitionerIDController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/pension/practitioner-ID                  controllers.employer.pension.DoYouHavePractitionerIDController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHavePractitionerID" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHavePractitionerID.title = Do you have a Scheme Practitioner ID?" >> ../conf/messages.en
echo "doYouHavePractitionerID.heading = Do you have a Scheme Practitioner ID?" >> ../conf/messages.en
echo "doYouHavePractitionerID.Yes = Yes" >> ../conf/messages.en
echo "doYouHavePractitionerID.No = No" >> ../conf/messages.en
echo "doYouHavePractitionerID.error.required = Select yes if you have a Scheme Practitioner ID" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHavePractitionerID" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHavePractitionerID.title = A oes gennych Ddynodydd (ID) Ymarferydd Cynllun?" >> ../conf/messages.cy
echo "doYouHavePractitionerID.heading = A oes gennych Ddynodydd (ID) Ymarferydd Cynllun?" >> ../conf/messages.cy
echo "doYouHavePractitionerID.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHavePractitionerID.No = Na" >> ../conf/messages.cy
echo "doYouHavePractitionerID.error.required = Dewiswch ‘Iawn’ os oes gennych Ddynodydd (ID) Ymarferydd Cynllun" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.pension.DoYouHavePractitionerIDNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHavePractitionerID completed"
