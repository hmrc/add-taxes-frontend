#!/bin/bash

echo "Applying migration HaveYouRegisteredForVATMOSS"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/moss/non-eu                  controllers.vat.moss.noneu.HaveYouRegisteredForVATMOSSController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss/non-eu                  controllers.vat.moss.noneu.HaveYouRegisteredForVATMOSSController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  HaveYouRegisteredForVATMOSS" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "haveYouRegisteredForVATMOSS.title = Have you already registered for VAT MOSS?" >> ../conf/messages.en
echo "haveYouRegisteredForVATMOSS.heading = Have you already registered for VAT MOSS?" >> ../conf/messages.en
echo "haveYouRegisteredForVATMOSS.Yes = Yes - I've already registered for VAT MOSS" >> ../conf/messages.en
echo "haveYouRegisteredForVATMOSS.No = No - I'm not registered for VAT MOSS yet" >> ../conf/messages.en
echo "haveYouRegisteredForVATMOSS.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  HaveYouRegisteredForVATMOSS" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "haveYouRegisteredForVATMOSS.title = A ydych eisoes wedi cofrestru ar gyfer GUC TAW?" >> ../conf/messages.cy
echo "haveYouRegisteredForVATMOSS.heading = A ydych eisoes wedi cofrestru ar gyfer GUC TAW?" >> ../conf/messages.cy
echo "haveYouRegisteredForVATMOSS.Yes = Ydw - rwyf eisoes wedi cofrestru ar gyfer GUC TAW" >> ../conf/messages.cy
echo "haveYouRegisteredForVATMOSS.No = Nac ydw - nid wyf wedi cofrestru ar gyfer GUC TAW ar hyn o bryd" >> ../conf/messages.cy
echo "haveYouRegisteredForVATMOSS.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.moss.noneu.HaveYouRegisteredForVATMOSSNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration HaveYouRegisteredForVATMOSS completed"
