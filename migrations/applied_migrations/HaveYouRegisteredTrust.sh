#!/bin/bash

echo "Applying migration HaveYouRegisteredTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /self-assessment/trust                  controllers.sa.trust.HaveYouRegisteredTrustController.onPageLoad()" >> ../conf/app.routes
echo "POST       /self-assessment/trust                  controllers.sa.trust.HaveYouRegisteredTrustController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  HaveYouRegisteredTrust" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "haveYouRegisteredTrust.title = Have you registered your trust?" >> ../conf/messages.en
echo "haveYouRegisteredTrust.heading = Have you registered your trust?" >> ../conf/messages.en
echo "haveYouRegisteredTrust.Yes = Yes - the trust is already registered" >> ../conf/messages.en
echo "haveYouRegisteredTrust.No = No - the trust isn't registered yet" >> ../conf/messages.en
echo "haveYouRegisteredTrust.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  HaveYouRegisteredTrust" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "haveYouRegisteredTrust.title = A ydych wedi cofrestru eich ymddiriedolaeth?" >> ../conf/messages.cy
echo "haveYouRegisteredTrust.heading = A ydych wedi cofrestru eich ymddiriedolaeth?" >> ../conf/messages.cy
echo "haveYouRegisteredTrust.Yes = Ydw - mae'r ymddiriedolaeth eisoes wedi'i chofrestru" >> ../conf/messages.cy
echo "haveYouRegisteredTrust.No = Nac ydw - nid yw'r ymddiriedolaeth wedi'i chofrestru ar hyn o bryd" >> ../conf/messages.cy
echo "haveYouRegisteredTrust.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
awk '/object/ {\
     print;\
     print "";\
     print "  implicit val haveYouRegisteredTrust: NextPage[HaveYouRegisteredTrustId.type,";\
     print "    models.sa.trust.HaveYouRegisteredTrust] = {";\
     print "    new NextPage[HaveYouRegisteredTrustId.type, models.sa.trust.HaveYouRegisteredTrust] {";\
     print "      override def get(b: models.sa.trust.HaveYouRegisteredTrust)(implicit urlHelper: UrlHelper): Call =";\
     print "        b match {";\
     print "          case models.sa.trust.HaveYouRegisteredTrust.Yes => ???";\
     print "          case models.sa.trust.HaveYouRegisteredTrust.No => ???";\
     print "        }";\
     print "     }";\
     print "  }";\
     next }1' ../app/utils/NextPage.scala > tmp && mv tmp ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouRegisteredTrust completed"
