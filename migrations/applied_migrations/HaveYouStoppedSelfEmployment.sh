#!/bin/bash

echo "Applying migration HaveYouStoppedSelfEmployment"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assesssment/how-to-stop-sa                  controllers.deenrolment.HaveYouStoppedSelfEmploymentController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assesssment/how-to-stop-sa                  controllers.deenrolment.HaveYouStoppedSelfEmploymentController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  HaveYouStoppedSelfEmployment" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "haveYouStoppedSelfEmployment.title = Have you stopped self-employment?" >> ../conf/messages.en
echo "haveYouStoppedSelfEmployment.heading = Have you stopped self-employment?" >> ../conf/messages.en
echo "haveYouStoppedSelfEmployment.Yes = Yes" >> ../conf/messages.en
echo "haveYouStoppedSelfEmployment.No = No, I need to stop using the Self Assessment online service" >> ../conf/messages.en
echo "haveYouStoppedSelfEmployment.error.required = Select yes if you have stopped self-employment" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  HaveYouStoppedSelfEmployment" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "haveYouStoppedSelfEmployment.title = A ydych wedi rhoi’r gorau i hunangyflogaeth?" >> ../conf/messages.cy
echo "haveYouStoppedSelfEmployment.heading = A ydych wedi rhoi’r gorau i hunangyflogaeth?" >> ../conf/messages.cy
echo "haveYouStoppedSelfEmployment.Yes = lawn" >> ../conf/messages.cy
echo "haveYouStoppedSelfEmployment.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth Hunanasesiad ar-lein" >> ../conf/messages.cy
echo "haveYouStoppedSelfEmployment.error.required = Dewiswch Iawn os ydych wedi rhoi’r gorau i hunangyflogaeth" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.HaveYouStoppedSelfEmploymentNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouStoppedSelfEmployment completed"
