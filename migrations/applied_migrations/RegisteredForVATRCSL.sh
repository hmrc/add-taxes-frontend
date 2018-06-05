#!/bin/bash

echo "Applying migration RegisteredForVATRCSL"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/rcsl                       controllers.vat.rcsl.RegisteredForVATRCSLController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/rcsl                       controllers.vat.rcsl.RegisteredForVATRCSLController.onSubmit()"   >> ../conf/app.routes

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.rcsl.RegisteredForVATRCSLNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredForVATRCSL completed"
