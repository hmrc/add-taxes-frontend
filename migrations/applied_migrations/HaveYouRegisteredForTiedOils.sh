#!/bin/bash

echo "Applying migration HaveYouRegisteredForTiedOils"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /haveYouRegisteredForTiedOils               controllers.other.oil.HaveYouRegisteredForTiedOilsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /haveYouRegisteredForTiedOils               controllers.other.oil.HaveYouRegisteredForTiedOilsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeHaveYouRegisteredForTiedOils               controllers.other.oil.HaveYouRegisteredForTiedOilsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeHaveYouRegisteredForTiedOils               controllers.other.oil.HaveYouRegisteredForTiedOilsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "haveYouRegisteredForTiedOils.title = haveYouRegisteredForTiedOils" >> ../conf/messages.en
echo "haveYouRegisteredForTiedOils.heading = haveYouRegisteredForTiedOils" >> ../conf/messages.en
echo "haveYouRegisteredForTiedOils.option1 = haveYouRegisteredForTiedOils" Option 1 >> ../conf/messages.en
echo "haveYouRegisteredForTiedOils.option2 = haveYouRegisteredForTiedOils" Option 2 >> ../conf/messages.en
echo "haveYouRegisteredForTiedOils.checkYourAnswersLabel = haveYouRegisteredForTiedOils" >> ../conf/messages.en
echo "haveYouRegisteredForTiedOils.error.required = Please give an answer for haveYouRegisteredForTiedOils" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def haveYouRegisteredForTiedOils: Option[HaveYouRegisteredForTiedOils] = cacheMap.getEntry[HaveYouRegisteredForTiedOils](HaveYouRegisteredForTiedOilsId.toString)";\
     print "";\
     next }1' ../app/utils/UserAnswers.scala > tmp && mv tmp ../app/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def haveYouRegisteredForTiedOils: Option[AnswerRow] = userAnswers.haveYouRegisteredForTiedOils map {";\
     print "    x => AnswerRow(\"haveYouRegisteredForTiedOils.checkYourAnswersLabel\", s\"haveYouRegisteredForTiedOils.$x\", true, routes.HaveYouRegisteredForTiedOilsController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouRegisteredForTiedOils completed"
