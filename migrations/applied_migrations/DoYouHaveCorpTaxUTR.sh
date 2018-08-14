#!/bin/bash

echo "Applying migration DoYouHaveCorpTaxUTR"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /corporation-tax/have-utr                  controllers.corporation.DoYouHaveCorpTaxUTRController.onPageLoad()" >> ../conf/app.routes
echo "POST       /corporation-tax/have-utr                  controllers.corporation.DoYouHaveCorpTaxUTRController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  DoYouHaveCorpTaxUTR" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "doYouHaveCorpTaxUTR.title = Do you have a Corporation Tax Unique Taxpayer Reference (UTR)?" >> ../conf/messages.en
echo "doYouHaveCorpTaxUTR.heading = Do you have a Corporation Tax Unique Taxpayer Reference (UTR)?" >> ../conf/messages.en
echo "doYouHaveCorpTaxUTR.Yes = Yes" >> ../conf/messages.en
echo "doYouHaveCorpTaxUTR.No = No" >> ../conf/messages.en
echo "doYouHaveCorpTaxUTR.error.required = Select yes if you have a Corporation Tax Unique Taxpayer Reference (UTR)" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  DoYouHaveCorpTaxUTR" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "doYouHaveCorpTaxUTR.title = A oes gennych Gyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Treth Gorfforaeth?" >> ../conf/messages.cy
echo "doYouHaveCorpTaxUTR.heading = A oes gennych Gyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Treth Gorfforaeth?" >> ../conf/messages.cy
echo "doYouHaveCorpTaxUTR.Yes = Iawn" >> ../conf/messages.cy
echo "doYouHaveCorpTaxUTR.No = Na" >> ../conf/messages.cy
echo "doYouHaveCorpTaxUTR.error.required = Dewiswch ‘Iawn’ os oes gennych Gyfeirnod Unigryw y Trethdalwr (UTR) ar gyfer Treth Gorfforaeth" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.corporation.DoYouHaveCorpTaxUTRNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveCorpTaxUTR completed"
