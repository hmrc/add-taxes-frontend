#!/bin/bash

echo "Applying migration DoYouHaveActivationToken"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/pension/activation-token                  controllers.employer.pension.DoYouHaveActivationTokenController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/pension/activation-token                  controllers.employer.pension.DoYouHaveActivationTokenController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveActivationToken" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveActivationToken.title = Do you have an activation token?" >> ../conf/messages.en
echo "doYouHaveActivationToken.heading = Do you have an activation token?" >> ../conf/messages.en
echo "doYouHaveActivationToken.Yes = Yes" >> ../conf/messages.en
echo "doYouHaveActivationToken.No = No" >> ../conf/messages.en
echo "doYouHaveActivationToken.error.required = Select yes if you have an activation token" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveActivationToken" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveActivationToken.title = A oes gennych docyn cychwyn?" >> ../conf/messages.cy
echo "doYouHaveActivationToken.heading = A oes gennych docyn cychwyn?" >> ../conf/messages.cy
echo "doYouHaveActivationToken.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHaveActivationToken.No = Na" >> ../conf/messages.cy
echo "doYouHaveActivationToken.error.required = Dewiswch ‘Iawn’ os oes gennych docyn cychwyn" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.pension.DoYouHaveActivationTokenNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveActivationToken completed"
