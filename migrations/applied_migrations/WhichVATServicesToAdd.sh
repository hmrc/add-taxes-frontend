#!/bin/bash

echo "Applying migration WhichVATServicesToAdd"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat                  controllers.vat.WhichVATServicesToAddController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat                  controllers.vat.WhichVATServicesToAddController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WhichVATServicesToAdd" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "whichVATServicesToAdd.title = Which VAT service do you want to add?" >> ../conf/messages.en
echo "whichVATServicesToAdd.heading = Which VAT service do you want to add?" >> ../conf/messages.en
echo "whichVATServicesToAdd.option1 = whichVATServicesToAdd" Option 1 >> ../conf/messages.en
echo "whichVATServicesToAdd.option2 = whichVATServicesToAdd" Option 2 >> ../conf/messages.en
echo "whichVATServicesToAdd.error.required = Select a VAT service" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WhichVATServicesToAdd" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "whichVATServicesToAdd.title = Pa wasanaeth TAW ydych eisiau ei ychwanegu?" >> ../conf/messages.cy
echo "whichVATServicesToAdd.heading = Pa wasanaeth TAW ydych eisiau ei ychwanegu?" >> ../conf/messages.cy
echo "whichVATServicesToAdd.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whichVATServicesToAdd.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whichVATServicesToAdd.error.required = Dewis gwasanaeth TAW" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.WhichVATServicesToAddNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichVATServicesToAdd completed"
