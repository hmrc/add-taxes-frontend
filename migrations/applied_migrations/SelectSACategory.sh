#!/bin/bash

echo "Applying migration SelectSACategory"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /                  controllers.sa.SelectSACategoryController.onPageLoad()" >> ../conf/app.routes
echo "POST       /                  controllers.sa.SelectSACategoryController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SelectSACategory" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "selectSACategory.title = Select a Self Assessment category" >> ../conf/messages.en
echo "selectSACategory.heading = Select a Self Assessment category" >> ../conf/messages.en
echo "selectSACategory.option1 = selectSACategory" Option 1 >> ../conf/messages.en
echo "selectSACategory.option2 = selectSACategory" Option 2 >> ../conf/messages.en
echo "selectSACategory.error.required = Select a category" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SelectSACategory" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "selectSACategory.title = Dewiswch gategori Hunanasesiad" >> ../conf/messages.cy
echo "selectSACategory.heading = Dewiswch gategori Hunanasesiad" >> ../conf/messages.cy
echo "selectSACategory.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectSACategory.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectSACategory.error.required = Dewis categori" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val selectSACategory: NextPage[SelectSACategoryId.type,";\
     print "    models.sa.SelectSACategory] = {";\
     print "    new NextPage[SelectSACategoryId.type, models.sa.SelectSACategory] {";\
     print "      override def get(b: models.sa.SelectSACategory)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.sa.SelectSACategory.Option1 => ???";\
     print "          case models.sa.SelectSACategory.Option2 => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SelectSACategory completed"
