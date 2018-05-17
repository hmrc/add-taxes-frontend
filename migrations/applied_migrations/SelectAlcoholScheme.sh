#!/bin/bash

echo "Applying migration SelectAlcoholScheme"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /other/alcohol                  controllers.other.alcohol.awrs.SelectAlcoholSchemeController.onPageLoad()" >> ../conf/app.routes
echo "POST       /other/alcohol                  controllers.other.alcohol.awrs.SelectAlcoholSchemeController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  SelectAlcoholScheme" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "selectAlcoholScheme.title = Select an alcohol scheme" >> ../conf/messages.en
echo "selectAlcoholScheme.heading = Select an alcohol scheme" >> ../conf/messages.en
echo "selectAlcoholScheme.option1 = selectAlcoholScheme" Option 1 >> ../conf/messages.en
echo "selectAlcoholScheme.option2 = selectAlcoholScheme" Option 2 >> ../conf/messages.en
echo "selectAlcoholScheme.error.required = Select a scheme" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  SelectAlcoholScheme" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "selectAlcoholScheme.title = Dewis cynllun alcohol" >> ../conf/messages.cy
echo "selectAlcoholScheme.heading = Dewis cynllun alcohol" >> ../conf/messages.cy
echo "selectAlcoholScheme.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectAlcoholScheme.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "selectAlcoholScheme.error.required = Dewis cynllun" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val selectAlcoholScheme: NextPage[SelectAlcoholSchemeId.type,";\
     print "    models.other.alcohol.awrs.SelectAlcoholScheme] = {";\
     print "    new NextPage[SelectAlcoholSchemeId.type, models.other.alcohol.awrs.SelectAlcoholScheme] {";\
     print "      override def get(b: models.other.alcohol.awrs.SelectAlcoholScheme)(implicit urlHelper: UrlHelper, request: Request[_]): Call =";\
     print "        b match {";\
     print "          case models.other.alcohol.awrs.SelectAlcoholScheme.Option1 => ???";\
     print "          case models.other.alcohol.awrs.SelectAlcoholScheme.Option2 => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SelectAlcoholScheme completed"
