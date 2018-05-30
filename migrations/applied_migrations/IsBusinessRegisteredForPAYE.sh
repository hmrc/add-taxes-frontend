#!/bin/bash

echo "Applying migration IsBusinessRegisteredForPAYE"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /employer/ers                       controllers.employer.ers.IsBusinessRegisteredForPAYEController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/ers                       controllers.employer.ers.IsBusinessRegisteredForPAYEController.onSubmit()"   >> ../conf/app.routes

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.cis.uk.contractor.IsBusinessRegisteredForPAYENextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration IsBusinessRegisteredForPAYE completed"
