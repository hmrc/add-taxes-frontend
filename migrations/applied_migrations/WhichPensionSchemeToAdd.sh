#!/bin/bash

echo "Applying migration WhichPensionSchemeToAdd"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        employer/pension                  controllers.employer.pension.WhichPensionSchemeToAddController.onPageLoad()" >> ../conf/app.routes
echo "POST       employer/pension                  controllers.employer.pension.WhichPensionSchemeToAddController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WhichPensionSchemeToAdd" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "whichPensionSchemeToAdd.title = Which pension scheme do you want to add?" >> ../conf/messages.en
echo "whichPensionSchemeToAdd.heading = Which pension scheme do you want to add?" >> ../conf/messages.en
echo "whichPensionSchemeToAdd.option1 = whichPensionSchemeToAdd" Option 1 >> ../conf/messages.en
echo "whichPensionSchemeToAdd.option2 = whichPensionSchemeToAdd" Option 2 >> ../conf/messages.en
echo "whichPensionSchemeToAdd.error.required = Select a scheme" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WhichPensionSchemeToAdd" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "whichPensionSchemeToAdd.title = Pa gynllun pensiwn ydych eisiau ei ychwanegu?" >> ../conf/messages.cy
echo "whichPensionSchemeToAdd.heading = Pa gynllun pensiwn ydych eisiau ei ychwanegu?" >> ../conf/messages.cy
echo "whichPensionSchemeToAdd.option1 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whichPensionSchemeToAdd.option2 = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "whichPensionSchemeToAdd.error.required = Dewis cynllun" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val whichPensionSchemeToAdd: NextPage[WhichPensionSchemeToAddId.type,";\
     print "    models.employer.pension.WhichPensionSchemeToAdd] = {";\
     print "    new NextPage[WhichPensionSchemeToAddId.type, models.employer.pension.WhichPensionSchemeToAdd] {";\
     print "      override def get(b: models.employer.pension.WhichPensionSchemeToAdd)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.employer.pension.WhichPensionSchemeToAdd.Option1 => ???";\
     print "          case models.employer.pension.WhichPensionSchemeToAdd.Option2 => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhichPensionSchemeToAdd completed"
