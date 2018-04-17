#!/bin/bash

echo "Applying migration RegisterExciseMovementControlSystem"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /registerExciseMovementControlSystem                       controllers.RegisterExciseMovementControlSystemController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  RegisterExciseMovementControlSystem" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "registerExciseMovementControlSystem.title = registerExciseMovementControlSystem" >> ../conf/messages.en
echo "registerExciseMovementControlSystem.heading = registerExciseMovementControlSystem" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  RegisterExciseMovementControlSystem" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "registerExciseMovementControlSystem.title = WELSH NEEDED HERE" >> ../conf/messages.cy
echo "registerExciseMovementControlSystem.heading = WELSH NEEDED HERE" >> ../conf/messages.cy

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration RegisterExciseMovementControlSystem completed"
