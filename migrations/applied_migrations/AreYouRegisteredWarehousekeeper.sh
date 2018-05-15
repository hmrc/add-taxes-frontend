#!/bin/bash

echo "Applying migration AreYouRegisteredWarehousekeeper"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/alcohol/atwd                  controllers.other.alcohol.atwd.AreYouRegisteredWarehousekeeperController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/alcohol/atwd                  controllers.other.alcohol.atwd.AreYouRegisteredWarehousekeeperController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AreYouRegisteredWarehousekeeper" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "areYouRegisteredWarehousekeeper.title = Are you a registered excise warehousekeeper?" >> ../conf/messages.en
echo "areYouRegisteredWarehousekeeper.heading = Are you a registered excise warehousekeeper?" >> ../conf/messages.en
echo "areYouRegisteredWarehousekeeper.Yes = Yes - I have a warehouse ID" >> ../conf/messages.en
echo "areYouRegisteredWarehousekeeper.No = No - I'm not a registered warehousekeeper" >> ../conf/messages.en
echo "areYouRegisteredWarehousekeeper.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AreYouRegisteredWarehousekeeper" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "areYouRegisteredWarehousekeeper.title = A ydych yn geidwad warws ecséis cofrestredig?" >> ../conf/messages.cy
echo "areYouRegisteredWarehousekeeper.heading = A ydych yn geidwad warws ecséis cofrestredig?" >> ../conf/messages.cy
echo "areYouRegisteredWarehousekeeper.Yes = Ydw - mae gennyf ID warws" >> ../conf/messages.cy
echo "areYouRegisteredWarehousekeeper.No = Nac ydw - nid wyf yn geidwad warws cofrestredig" >> ../conf/messages.cy
echo "areYouRegisteredWarehousekeeper.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val areYouRegisteredWarehousekeeper: NextPage[AreYouRegisteredWarehousekeeperId.type,";\
     print "    models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper] = {";\
     print "    new NextPage[AreYouRegisteredWarehousekeeperId.type, models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper] {";\
     print "      override def get(b: models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper.Yes => ???";\
     print "          case models.other.alcohol.atwd.AreYouRegisteredWarehousekeeper.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AreYouRegisteredWarehousekeeper completed"
