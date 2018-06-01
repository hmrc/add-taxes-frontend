#!/bin/bash

echo "Applying migration DoesBusinessManagePAYEController"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/intermediaries/epaye                  controllers.employer.intermediaries.DoesBusinessManagePAYEControllerController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/intermediaries/epaye                  controllers.employer.intermediaries.DoesBusinessManagePAYEControllerController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoesBusinessManagePAYEController" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doesBusinessManagePAYEController.title = Does your business manage PAYE for employers online?" >> ../conf/messages.en
echo "doesBusinessManagePAYEController.heading = Does your business manage PAYE for employers online?" >> ../conf/messages.en
echo "doesBusinessManagePAYEController.Yes = Yes - we manage PAYE for employers online" >> ../conf/messages.en
echo "doesBusinessManagePAYEController.No = No - we don't manage PAYE for employers online" >> ../conf/messages.en
echo "doesBusinessManagePAYEController.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoesBusinessManagePAYEController" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doesBusinessManagePAYEController.title = A yw eich busnes yn rheoli TWE i gyflogwyr ar-lein?" >> ../conf/messages.cy
echo "doesBusinessManagePAYEController.heading = A yw eich busnes yn rheoli TWE i gyflogwyr ar-lein?" >> ../conf/messages.cy
echo "doesBusinessManagePAYEController.Yes = Ydym - rydym yn rheoli TWE i gyflogwyr ar-lein" >> ../conf/messages.cy
echo "doesBusinessManagePAYEController.No = Nac ydym - nid ydym yn rheoli TWE i gyflogwyr ar-lein" >> ../conf/messages.cy
echo "doesBusinessManagePAYEController.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.intermediaries.DoesBusinessManagePAYEControllerNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoesBusinessManagePAYEController completed"
