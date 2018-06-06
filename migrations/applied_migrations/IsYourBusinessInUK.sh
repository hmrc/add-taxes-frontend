#!/bin/bash

echo "Applying migration IsYourBusinessInUK"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis                  controllers.employer.cis.IsYourBusinessInUKController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis                  controllers.employer.cis.IsYourBusinessInUKController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  IsYourBusinessInUK" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "isYourBusinessInUK.title = Is your business based in the UK?" >> ../conf/messages.en
echo "isYourBusinessInUK.heading = Is your business based in the UK?" >> ../conf/messages.en
echo "isYourBusinessInUK.Yes = Yes - my business is based in the UK" >> ../conf/messages.en
echo "isYourBusinessInUK.No = No - my business isn't based in the UK" >> ../conf/messages.en
echo "isYourBusinessInUK.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  IsYourBusinessInUK" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "isYourBusinessInUK.title = A yw eich busnes wedi'i leoli yn y DU?" >> ../conf/messages.cy
echo "isYourBusinessInUK.heading = A yw eich busnes wedi'i leoli yn y DU?" >> ../conf/messages.cy
echo "isYourBusinessInUK.Yes = Nac ydy - nid yw fy musnes wedi'i leoli yn y DU" >> ../conf/messages.cy
echo "isYourBusinessInUK.No = Nac ydy - nid yw fy musnes wedi'i leoli yn y DU" >> ../conf/messages.cy
echo "isYourBusinessInUK.error.required = Ydy - mae fy musnes wedi'i leoli yn y DU" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.cis.IsYourBusinessInUKNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration IsYourBusinessInUK completed"
