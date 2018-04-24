#!/bin/bash

echo "Applying migration MyNewPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /                  controllers.MyNewPageController.onPageLoad()" >> ../conf/app.routes
echo "POST       /                     controllers.MyNewPageController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  MyNewPage" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "myNewPage.title = MyPageTitle" >> ../conf/messages.en
echo "myNewPage.heading = MyPageHeading" >> ../conf/messages.en
echo "myNewPage.option1 = myNewPage" Option 1 >> ../conf/messages.en
echo "myNewPage.option2 = myNewPage" Option 2 >> ../conf/messages.en
echo "myNewPage.error.required = ErrorMessage" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  MyNewPage" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "myNewPage.title = WelshPageTitle" >> ../conf/messages.cy
echo "myNewPage.heading = WelshPageHeading" >> ../conf/messages.cy
echo "myNewPage.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "myNewPage.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "myNewPage.error.required = WelshErrorMessage" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val myNewPage: NextPage[MyNewPageId.type,";\
     print "    MyNewPage] = {";\
     print "    new NextPage[MyNewPageId.type, MyNewPage] {";\
     print "      override def get(b: MyNewPage)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.MyNewPage.Option1 => routes.IndexController.onPageLoad()";\
     print "          case models.MyNewPage.Option2 => routes.IndexController.onPageLoad()";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration MyNewPage completed"
