#!/bin/bash

echo "Applying migration StopCorporationTax"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /ct/how-to-stop-ct                  controllers.deenrolment.StopCorporationTaxController.onPageLoad()" >> ../conf/app.routes
echo "POST       /ct/how-to-stop-ct                  controllers.deenrolment.StopCorporationTaxController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  StopCorporationTax" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "stopCorporationTax.title = What do you need to do?" >> ../conf/messages.en
echo "stopCorporationTax.heading = What do you need to do?" >> ../conf/messages.en
echo "stopCorporationTax.option1 = stopCorporationTax" Option 1 >> ../conf/messages.en
echo "stopCorporationTax.option2 = stopCorporationTax" Option 2 >> ../conf/messages.en
echo "stopCorporationTax.error.required = Select what you need to do" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  StopCorporationTax" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "stopCorporationTax.title = Beth sydd angen i chi’i wneud?" >> ../conf/messages.cy
echo "stopCorporationTax.heading = Beth sydd angen i chi’i wneud?" >> ../conf/messages.cy
echo "stopCorporationTax.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "stopCorporationTax.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "stopCorporationTax.error.required = Dewiswch yr hyn sydd angen i chi ei wneud" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.StopCorporationTaxNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration StopCorporationTax completed"
