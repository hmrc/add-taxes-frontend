#!/bin/bash

echo "Applying migration MyNewPage"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /                  controllers.org.change.MyNewPageController.onPageLoad()" >> ../conf/app.routes
echo "POST       /                  controllers.org.change.MyNewPageController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  MyNewPage" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "myNewPage.title = MyPageTitle" >> ../conf/messages.en
echo "myNewPage.heading = MyPageHeading" >> ../conf/messages.en
echo "myNewPage.Yes = Yes" >> ../conf/messages.en
echo "myNewPage.No = No" >> ../conf/messages.en
echo "myNewPage.error.required = ErrorMessage" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  MyNewPage" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "myNewPage.title = WelshPageTitle" >> ../conf/messages.cy
echo "myNewPage.heading = WelshPageHeading" >> ../conf/messages.cy
echo "myNewPage.Yes = Yawn" >> ../conf/messages.cy
echo "myNewPage.No = Na" >> ../conf/messages.cy
echo "myNewPage.error.required = WelshErrorMessage" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.org.change.MyNewPageNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration MyNewPage completed"
