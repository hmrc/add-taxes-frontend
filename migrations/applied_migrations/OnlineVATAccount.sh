#!/bin/bash

echo "Applying migration OnlineVATAccount"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/moss/uk/vat-registered                  controllers.vat.moss.uk.OnlineVATAccountController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss/uk/vat-registered                  controllers.vat.moss.uk.OnlineVATAccountController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  OnlineVATAccount" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "onlineVATAccount.title = Does your business have another online HMRC account for VAT?" >> ../conf/messages.en
echo "onlineVATAccount.heading = Does your business have another online HMRC account for VAT?" >> ../conf/messages.en
echo "onlineVATAccount.Yes = Yes - there's another online account for VAT" >> ../conf/messages.en
echo "onlineVATAccount.No = No - there isn't another online account for VAT" >> ../conf/messages.en
echo "onlineVATAccount.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  OnlineVATAccount" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "onlineVATAccount.title = A oes gan eich busnes gyfrif CThEM ar-lein arall ar gyfer TAW?" >> ../conf/messages.cy
echo "onlineVATAccount.heading = A oes gan eich busnes gyfrif CThEM ar-lein arall ar gyfer TAW?" >> ../conf/messages.cy
echo "onlineVATAccount.Yes = Oes - mae cyfrif ar-lein arall ar gyfer TAW" >> ../conf/messages.cy
echo "onlineVATAccount.No = Nac oes - does dim cyfrif ar-lein arall ar gyfer TAW" >> ../conf/messages.cy
echo "onlineVATAccount.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val onlineVATAccount: NextPage[OnlineVATAccountId.type,";\
     print "    models.vat.moss.uk.OnlineVATAccount] = {";\
     print "    new NextPage[OnlineVATAccountId.type, models.vat.moss.uk.OnlineVATAccount] {";\
     print "      override def get(b: models.vat.moss.uk.OnlineVATAccount)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.vat.moss.uk.OnlineVATAccount.Yes => ???";\
     print "          case models.vat.moss.uk.OnlineVATAccount.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration OnlineVATAccount completed"
