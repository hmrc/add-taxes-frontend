#!/bin/bash

echo "Applying migration WhatIsYourOrganisation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /vat/vat-giant                  controllers.vat.vat-giant.WhatIsYourOrganisationController.onPageLoad()" >> ../conf/app.routes
echo "POST       /vat/vat-giant                  controllers.vat.vat-giant.WhatIsYourOrganisationController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WhatIsYourOrganisation" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "whatIsYourOrganisation.title = Is your organisation an NHS trust, government department or Royal Household?" >> ../conf/messages.en
echo "whatIsYourOrganisation.heading = Is your organisation an NHS trust, government department or Royal Household?" >> ../conf/messages.en
echo "whatIsYourOrganisation.Yes = Yes" >> ../conf/messages.en
echo "whatIsYourOrganisation.No = No" >> ../conf/messages.en
echo "whatIsYourOrganisation.error.required = Select yes if your organisation is an NHS trust, government department or Royal Household" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WhatIsYourOrganisation" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "whatIsYourOrganisation.title = A yw’ch sefydliad yn ymddiriedolaeth y GIG, yn adran o’r Llywodraeth neu’n Aelwyd Frenhinol?" >> ../conf/messages.cy
echo "whatIsYourOrganisation.heading = A yw’ch sefydliad yn ymddiriedolaeth y GIG, yn adran o’r Llywodraeth neu’n Aelwyd Frenhinol?" >> ../conf/messages.cy
echo "whatIsYourOrganisation.Yes = Iawn" >> ../conf/messages.cy
echo "whatIsYourOrganisation.No = Na" >> ../conf/messages.cy
echo "whatIsYourOrganisation.error.required = Dewiswch ‘Iawn’ os yw’ch sefydliad yn ymddiriedolaeth y GIG, yn adran o’r Llywodraeth neu’n Aelwyd Frenhinol" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.vat.vat-giant.WhatIsYourOrganisationNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WhatIsYourOrganisation completed"
