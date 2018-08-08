#!/bin/bash

echo "Applying migration AlreadyRegisteredForVATMoss"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/moss                       controllers.vat.moss.ukbased.AlreadyRegisteredForVATMossController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss                       controllers.vat.moss.ukbased.AlreadyRegisteredForVATMossController.onSubmit()"   >> ../conf/app.routes

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.moss.ukbased.AlreadyRegisteredForVATMossNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AlreadyRegisteredForVATMoss completed"
