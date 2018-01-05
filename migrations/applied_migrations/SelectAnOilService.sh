#!/bin/bash

echo "Applying migration SelectAnOilService"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /selectAnOilService               controllers.SelectAnOilServiceController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /selectAnOilService               controllers.SelectAnOilServiceController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSelectAnOilService               controllers.SelectAnOilServiceController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSelectAnOilService               controllers.SelectAnOilServiceController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "selectAnOilService.title = selectAnOilService" >> ../conf/messages.en
echo "selectAnOilService.heading = selectAnOilService" >> ../conf/messages.en
echo "selectAnOilService.option1 = selectAnOilService" Option 1 >> ../conf/messages.en
echo "selectAnOilService.option2 = selectAnOilService" Option 2 >> ../conf/messages.en
echo "selectAnOilService.checkYourAnswersLabel = selectAnOilService" >> ../conf/messages.en
echo "selectAnOilService.error.required = Please give an answer for selectAnOilService" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def selectAnOilService: Option[SelectAnOilService] = cacheMap.getEntry[SelectAnOilService](SelectAnOilServiceId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def selectAnOilService: Option[AnswerRow] = userAnswers.selectAnOilService map {";\
     print "    x => AnswerRow(\"selectAnOilService.checkYourAnswersLabel\", s\"selectAnOilService.$x\", true, routes.SelectAnOilServiceController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SelectAnOilService completed"
