#!/bin/bash

echo "Applying migration StopFilingSelfAssessment"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assessment/how-to-stop-filing                  controllers.deenrolment.StopFilingSelfAssessmentController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assessment/how-to-stop-filing                  controllers.deenrolment.StopFilingSelfAssessmentController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  StopFilingSelfAssessment" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "stopFilingSelfAssessment.title = Do you need to stop filing Self Assessment returns?" >> ../conf/messages.en
echo "stopFilingSelfAssessment.heading = Do you need to stop filing Self Assessment returns?" >> ../conf/messages.en
echo "stopFilingSelfAssessment.Yes = Yes" >> ../conf/messages.en
echo "stopFilingSelfAssessment.No = No, I need to stop using the Self Assessment online service" >> ../conf/messages.en
echo "stopFilingSelfAssessment.error.required = Select yes if you need to stop filing Self Assessment returns" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  StopFilingSelfAssessment" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "stopFilingSelfAssessment.title = A oes angen i chi roi’r gorau i gyflwyno Ffurflenni Treth Hunanasesiad?" >> ../conf/messages.cy
echo "stopFilingSelfAssessment.heading = A oes angen i chi roi’r gorau i gyflwyno Ffurflenni Treth Hunanasesiad?" >> ../conf/messages.cy
echo "stopFilingSelfAssessment.Yes = Iawn" >> ../conf/messages.cy
echo "stopFilingSelfAssessment.No = Na, mae angen i fi roi’r gorau i ddefnyddio’r gwasanaeth Hunanasesiad ar-lein" >> ../conf/messages.cy
echo "stopFilingSelfAssessment.error.required = A oes angen i chi roi’r gorau i gyflwyno Ffurflenni Treth Hunanasesiad?" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.deenrolment.StopFilingSelfAssessmentNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration StopFilingSelfAssessment completed"
