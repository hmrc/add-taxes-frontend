#!/bin/bash

echo "Applying migration RegisteredForVATEURefunds"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /vat/eurefunds                       controllers.vat.eurefunds.RegisteredForVATEURefundsController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/eurefunds                       controllers.vat.eurefunds.RegisteredForVATEURefundsController.onSubmit()"   >> ../conf/app.routes

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.eurefunds.RegisteredForVATEURefundsNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisteredForVATEURefunds completed"
