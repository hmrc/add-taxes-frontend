#!/bin/bash

echo "Applying migration HaveYouRegisteredForRebatedOils"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /haveYouRegisteredForRebatedOils               controllers.other.oil.HaveYouRegisteredForRebatedOilsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /haveYouRegisteredForRebatedOils               controllers.other.oil.HaveYouRegisteredForRebatedOilsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHaveYouRegisteredForRebatedOils               controllers.other.oil.HaveYouRegisteredForRebatedOilsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHaveYouRegisteredForRebatedOils               controllers.other.oil.HaveYouRegisteredForRebatedOilsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "haveYouRegisteredForRebatedOils.title = haveYouRegisteredForRebatedOils" >> ../conf/messages.en
echo "haveYouRegisteredForRebatedOils.heading = haveYouRegisteredForRebatedOils" >> ../conf/messages.en
echo "haveYouRegisteredForRebatedOils.option1 = haveYouRegisteredForRebatedOils" Option 1 >> ../conf/messages.en
echo "haveYouRegisteredForRebatedOils.option2 = haveYouRegisteredForRebatedOils" Option 2 >> ../conf/messages.en
echo "haveYouRegisteredForRebatedOils.checkYourAnswersLabel = haveYouRegisteredForRebatedOils" >> ../conf/messages.en
echo "haveYouRegisteredForRebatedOils.error.required = Please give an answer for haveYouRegisteredForRebatedOils" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def haveYouRegisteredForRebatedOils: Option[HaveYouRegisteredForRebatedOils] = cacheMap.getEntry[HaveYouRegisteredForRebatedOils](HaveYouRegisteredForRebatedOilsId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def haveYouRegisteredForRebatedOils: Option[AnswerRow] = userAnswers.haveYouRegisteredForRebatedOils map {";\
     print "    x => AnswerRow(\"haveYouRegisteredForRebatedOils.checkYourAnswersLabel\", s\"haveYouRegisteredForRebatedOils.$x\", true, routes.HaveYouRegisteredForRebatedOilsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouRegisteredForRebatedOils completed"
