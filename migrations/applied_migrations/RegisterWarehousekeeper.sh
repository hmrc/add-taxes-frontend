#!/bin/bash

echo "Applying migration RegisterWarehousekeeper"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/alcohol/atwd/register                       controllers.other.alcohol.atwd.RegisterWarehousekeeperController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterWarehousekeeper" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerWarehousekeeper.title = Register as an excise warehousekeeper first" >> ../conf/messages.en
echo "registerWarehousekeeper.heading = Register as an excise warehousekeeper first" >> ../conf/messages.en
echo "registerWarehousekeeper.continue = Continue - register as an excise warehousekeeper" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterWarehousekeeper" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerWarehousekeeper.title = Cofrestrwch fel ceidwad warws ecséis yn gyntaf" >> ../conf/messages.cy
echo "registerWarehousekeeper.heading = Cofrestrwch fel ceidwad warws ecséis yn gyntaf" >> ../conf/messages.cy
echo "registerWarehousekeeper.continue = Mynd yn eich blaen - cofrestru fel ceidwad warws ecséis" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterWarehousekeeper completed"
