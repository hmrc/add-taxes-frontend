#!/bin/bash

echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        $url$                       controllers.$package$.$className$Controller.onPageLoad()" >> ../conf/app.routes
echo "POST       $url$                       controllers.$package$.$className$Controller.onSubmit()"   >> ../conf/app.routes

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val $className;format="decap"$: NextPage[$className$Id.type, models.$modelPackage$.$model$] = {";\
     print "    new NextPage[$className$Id.type, models.$modelPackage$.$model$] {";\
     print "      override def get(b: models.$modelPackage$.$model$)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case _ => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration $className;format="snake"$ completed"
