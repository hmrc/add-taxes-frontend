#!/bin/bash

echo "Applying migration PaperForms"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/land/stamp-duty/paper-forms                       controllers.other.land.stampduty.PaperFormsController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  PaperForms" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "paperForms.title = Use paper forms" >> ../conf/messages.en
echo "paperForms.heading = Use paper forms" >> ../conf/messages.en
echo "paperForms.continue = Continue to paper forms" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  PaperForms" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "paperForms.title = Defnyddiwch ffurflenni papur" >> ../conf/messages.cy
echo "paperForms.heading = Defnyddiwch ffurflenni papur" >> ../conf/messages.cy
echo "paperForms.continue = Mynd yn eich blaen i ffurflenni papur" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration PaperForms completed"
