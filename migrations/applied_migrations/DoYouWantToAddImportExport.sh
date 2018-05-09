#!/bin/bash

echo "Applying migration DoYouWantToAddImportExport"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/import-export                  controllers.other.importexports.DoYouWantToAddImportExportController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/import-export                  controllers.other.importexports.DoYouWantToAddImportExportController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouWantToAddImportExport" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouWantToAddImportExport.title = What do you want to add?" >> ../conf/messages.en
echo "doYouWantToAddImportExport.heading = What do you want to add?" >> ../conf/messages.en
echo "doYouWantToAddImportExport.option1 = doYouWantToAddImportExport" Option 1 >> ../conf/messages.en
echo "doYouWantToAddImportExport.option2 = doYouWantToAddImportExport" Option 2 >> ../conf/messages.en
echo "doYouWantToAddImportExport.error.required = Select a scheme" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouWantToAddImportExport" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouWantToAddImportExport.title = Beth ydych am ei ychwanegu?" >> ../conf/messages.cy
echo "doYouWantToAddImportExport.heading = Beth ydych am ei ychwanegu?" >> ../conf/messages.cy
echo "doYouWantToAddImportExport.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouWantToAddImportExport.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "doYouWantToAddImportExport.error.required = Dewis cynllun" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val doYouWantToAddImportExport: NextPage[DoYouWantToAddImportExportId.type,";\
     print "    models.other.importexports.DoYouWantToAddImportExport] = {";\
     print "    new NextPage[DoYouWantToAddImportExportId.type, models.other.importexports.DoYouWantToAddImportExport] {";\
     print "      override def get(b: models.other.importexports.DoYouWantToAddImportExport)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.other.importexports.DoYouWantToAddImportExport.Option1 => ???";\
     print "          case models.other.importexports.DoYouWantToAddImportExport.Option2 => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouWantToAddImportExport completed"
