#!/bin/bash

echo "Applying migration AlreadyRegisteredForVATMoss"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/moss/iom/vat-registered                  controllers.vat.moss.iom.AlreadyRegisteredForVATMossController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/moss/iom/vat-registered                  controllers.vat.moss.iom.AlreadyRegisteredForVATMossController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  AlreadyRegisteredForVATMoss" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "alreadyRegisteredForVATMoss.title = Have you already registered for VAT MOSS?" >> ../conf/messages.en
echo "alreadyRegisteredForVATMoss.heading = Have you already registered for VAT MOSS?" >> ../conf/messages.en
echo "alreadyRegisteredForVATMoss.Yes = Yes - I've already registered for VAT MOSS" >> ../conf/messages.en
echo "alreadyRegisteredForVATMoss.No = No - I'm not registered for VAT MOSS yet" >> ../conf/messages.en
echo "alreadyRegisteredForVATMoss.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  AlreadyRegisteredForVATMoss" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "alreadyRegisteredForVATMoss.title = A ydych eisoes wedi cofrestru ar gyfer GUC TAW?" >> ../conf/messages.cy
echo "alreadyRegisteredForVATMoss.heading = A ydych eisoes wedi cofrestru ar gyfer GUC TAW?" >> ../conf/messages.cy
echo "alreadyRegisteredForVATMoss.Yes = Ydw - rwyf eisoes wedi cofrestru ar gyfer GUC TAW" >> ../conf/messages.cy
echo "alreadyRegisteredForVATMoss.No = Nac ydw - nid wyf wedi cofrestru ar gyfer GUC TAW ar hyn o bryd" >> ../conf/messages.cy
echo "alreadyRegisteredForVATMoss.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.moss.iom.AlreadyRegisteredForVATMossNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration AlreadyRegisteredForVATMoss completed"
