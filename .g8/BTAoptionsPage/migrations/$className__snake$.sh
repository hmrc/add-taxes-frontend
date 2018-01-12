#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /$className;format="decap"$               controllers.$className$Controller.onPageLoad()" >> ../conf/app.routes
echo "POST       /$className;format="decap"$               controllers.$className$Controller.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  $className$" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.option1 = $className;format="decap"$" Option 1 >> ../conf/messages.en
echo "$className;format="decap"$.option2 = $className;format="decap"$" Option 2 >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Please give an answer for $className;format="decap"$" >> ../conf/messages.en

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  $className$" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "$className;format="decap"$.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.heading = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.checkYourAnswersLabel = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "$className;format="decap"$.error.required = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val $className;format="decap"$: NextPage[$className$Id.type,";\
     print "    $className$] = {";\
     print "    new NextPage[$className$Id.type, $className$] {";\
     print "      override def get(b: $className$)(implicit emacHelper: EmacHelper): Call =";\
     print "        b match {";\
     print "          case models.$className$.Option1 => routes.IndexController.onPageLoad()";\
     print "          case models.$className$.Option2 => routes.IndexController.onPageLoad()";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
