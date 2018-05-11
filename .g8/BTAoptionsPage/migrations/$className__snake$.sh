#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        $url$                  controllers.$package$.$className$Controller.onPageLoad()" >> ../conf/app.routes
echo "POST       $url$                  controllers.$package$.$className$Controller.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  $className$" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "$className;format="decap"$.title = $pageTitle$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $pageHeading$" >> ../conf/messages.en
echo "$className;format="decap"$.option1 = $className;format="decap"$" Option 1 >> ../conf/messages.en
echo "$className;format="decap"$.option2 = $className;format="decap"$" Option 2 >> ../conf/messages.en
echo "$className;format="decap"$.error.required = $errorMessage$" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  $className$" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "$className;format="decap"$.title = $welshPageTitle$" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = $welshPageHeading$" >> ../conf/messages.cy
echo "$className;format="decap"$.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.error.required = $welshErrorMessage$" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val $className;format="decap"$: NextPage[$className$Id.type,";\
     print "    models.$package$.$className$] = {";\
     print "    new NextPage[$className$Id.type, models.$package$.$className$] {";\
     print "      override def get(b: models.$package$.$className$)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.$package$.$className$.Option1 => ???";\
     print "          case models.$package$.$className$.Option2 => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
