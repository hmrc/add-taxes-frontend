#!/bin/bash

echo "Applying migration WhatTypeOfSubcontractor"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis/uk/subcontractor                  controllers.employer.cis.uk.subcontractor.WhatTypeOfSubcontractorController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis/uk/subcontractor                  controllers.employer.cis.uk.subcontractor.WhatTypeOfSubcontractorController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WhatTypeOfSubcontractor" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "whatTypeOfSubcontractor.title = What type of subcontractor are you?" >> ../conf/messages.en
echo "whatTypeOfSubcontractor.heading = What type of subcontractor are you?" >> ../conf/messages.en
echo "whatTypeOfSubcontractor.option1 = whatTypeOfSubcontractor" Option 1 >> ../conf/messages.en
echo "whatTypeOfSubcontractor.option2 = whatTypeOfSubcontractor" Option 2 >> ../conf/messages.en
echo "whatTypeOfSubcontractor.error.required = Select a business type" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WhatTypeOfSubcontractor" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "whatTypeOfSubcontractor.title = Pa fath o isgontractiwr ydych chi?" >> ../conf/messages.cy
echo "whatTypeOfSubcontractor.heading = Pa fath o isgontractiwr ydych chi?" >> ../conf/messages.cy
echo "whatTypeOfSubcontractor.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whatTypeOfSubcontractor.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whatTypeOfSubcontractor.error.required = Dewis math o fusnes" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.cis.uk.subcontractor.WhatTypeOfSubcontractorNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhatTypeOfSubcontractor completed"
