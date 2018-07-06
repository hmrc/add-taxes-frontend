#!/bin/bash

echo "Applying migration DoYouWantToLeaveCIS"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /cis/how-to-stop-cis                  controllers.deenrolment.DoYouWantToLeaveCISController.onPageLoad()" >> ../conf/app.routes
echo "POST       /cis/how-to-stop-cis                  controllers.deenrolment.DoYouWantToLeaveCISController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouWantToLeaveCIS" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouWantToLeaveCIS.title = Do you need to leave the Construction Industry Scheme (CIS)?" >> ../conf/messages.en
echo "doYouWantToLeaveCIS.heading = Do you need to leave the Construction Industry Scheme (CIS)?" >> ../conf/messages.en
echo "doYouWantToLeaveCIS.Yes = Yes, I have stopped trading as a contractor or subcontractor" >> ../conf/messages.en
echo "doYouWantToLeaveCIS.No = No, I need to stop using the online service" >> ../conf/messages.en
echo "doYouWantToLeaveCIS.error.required = Select yes if you need to leave the Construction Industry Scheme" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouWantToLeaveCIS" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouWantToLeaveCIS.title = A oes angen i chi adael Cynllun y Diwydiant Adeiladu (CIS)?" >> ../conf/messages.cy
echo "doYouWantToLeaveCIS.heading = A oes angen i chi adael Cynllun y Diwydiant Adeiladu (CIS)?" >> ../conf/messages.cy
echo "doYouWantToLeaveCIS.Yes = Iawn, rwyf wedi rhoi’r gorau i fod yn gontractwr neu isgontractwr" >> ../conf/messages.cy
echo "doYouWantToLeaveCIS.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth ar-lein" >> ../conf/messages.cy
echo "doYouWantToLeaveCIS.error.required = Dewiswch Iawn os oes angen i chi adael Cynllun y Diwydiant Adeiladu" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.DoYouWantToLeaveCISNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouWantToLeaveCIS completed"
