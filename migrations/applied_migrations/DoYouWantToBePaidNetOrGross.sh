#!/bin/bash

echo "Applying migration DoYouWantToBePaidNetOrGross"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis/uk/subcontractor/sole-trader/high-turnover                  controllers.employer.cis.uk.subcontractor.DoYouWantToBePaidNetOrGrossController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis/uk/subcontractor/sole-trader/high-turnover                  controllers.employer.cis.uk.subcontractor.DoYouWantToBePaidNetOrGrossController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouWantToBePaidNetOrGross" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouWantToBePaidNetOrGross.title = Do you want to be paid net or gross?" >> ../conf/messages.en
echo "doYouWantToBePaidNetOrGross.heading = Do you want to be paid net or gross?" >> ../conf/messages.en
echo "doYouWantToBePaidNetOrGross.option1 = doYouWantToBePaidNetOrGross" Option 1 >> ../conf/messages.en
echo "doYouWantToBePaidNetOrGross.option2 = doYouWantToBePaidNetOrGross" Option 2 >> ../conf/messages.en
echo "doYouWantToBePaidNetOrGross.error.required = You must make a selection" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouWantToBePaidNetOrGross" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouWantToBePaidNetOrGross.title = A ydych eisiau cael eich talu'n net neu'n gros?" >> ../conf/messages.cy
echo "doYouWantToBePaidNetOrGross.heading = A ydych eisiau cael eich talu'n net neu'n gros?" >> ../conf/messages.cy
echo "doYouWantToBePaidNetOrGross.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouWantToBePaidNetOrGross.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouWantToBePaidNetOrGross.error.required = Mae'n rhaid i chi wneud dewis" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.cis.uk.subcontractor.DoYouWantToBePaidNetOrGrossNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouWantToBePaidNetOrGross completed"
