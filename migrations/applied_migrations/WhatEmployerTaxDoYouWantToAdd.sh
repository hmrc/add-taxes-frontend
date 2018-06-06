#!/bin/bash

echo "Applying migration WhatEmployerTaxDoYouWantToAdd"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer                  controllers.employer.WhatEmployerTaxDoYouWantToAddController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer                  controllers.employer.WhatEmployerTaxDoYouWantToAddController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WhatEmployerTaxDoYouWantToAdd" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "whatEmployerTaxDoYouWantToAdd.title = Which employer tax do you want to add?" >> ../conf/messages.en
echo "whatEmployerTaxDoYouWantToAdd.heading = Which employer tax do you want to add?" >> ../conf/messages.en
echo "whatEmployerTaxDoYouWantToAdd.option1 = whatEmployerTaxDoYouWantToAdd" Option 1 >> ../conf/messages.en
echo "whatEmployerTaxDoYouWantToAdd.option2 = whatEmployerTaxDoYouWantToAdd" Option 2 >> ../conf/messages.en
echo "whatEmployerTaxDoYouWantToAdd.error.required = Select a tax type" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WhatEmployerTaxDoYouWantToAdd" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "whatEmployerTaxDoYouWantToAdd.title = Pa dreth y cyflogwr ydych eisiau ei hychwanegu?" >> ../conf/messages.cy
echo "whatEmployerTaxDoYouWantToAdd.heading = Pa dreth y cyflogwr ydych eisiau ei hychwanegu?" >> ../conf/messages.cy
echo "whatEmployerTaxDoYouWantToAdd.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whatEmployerTaxDoYouWantToAdd.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whatEmployerTaxDoYouWantToAdd.error.required = Dewis y math o dreth" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.WhatEmployerTaxDoYouWantToAddNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhatEmployerTaxDoYouWantToAdd completed"
