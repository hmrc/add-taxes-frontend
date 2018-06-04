#!/bin/bash

echo "Applying migration WasTurnoverMoreAfterVAT"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /employer/cis/uk/subcontractor/sole-trader                  controllers.employer.cis.uk.subcontractor.WasTurnoverMoreAfterVATController.onPageLoad()" >> ../conf/app.routes
echo "POST       /employer/cis/uk/subcontractor/sole-trader                  controllers.employer.cis.uk.subcontractor.WasTurnoverMoreAfterVATController.onSubmit()" >> ../conf/app.routes

echo "Adding messages to conf.messages (English)"
echo "" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "##  WasTurnoverMoreAfterVAT" >> ../conf/messages.en
echo "#######################################################" >> ../conf/messages.en
echo "wasTurnoverMoreAfterVAT.title = Was your turnover for the last 12 months more than £30,000 after VAT?" >> ../conf/messages.en
echo "wasTurnoverMoreAfterVAT.heading = Was your turnover for the last 12 months more than £30,000 after VAT?" >> ../conf/messages.en
echo "wasTurnoverMoreAfterVAT.Yes = Yes - my turnover was more than £30,000" >> ../conf/messages.en
echo "wasTurnoverMoreAfterVAT.No = No - my turnover was less than £30,000" >> ../conf/messages.en
echo "wasTurnoverMoreAfterVAT.error.required = Select yes or no" >> ../conf/messages.en

echo "Adding messages to conf.messages (Welsh)"
echo "" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "##  WasTurnoverMoreAfterVAT" >> ../conf/messages.cy
echo "#######################################################" >> ../conf/messages.cy
echo "wasTurnoverMoreAfterVAT.title = A oedd eich trosiant ar gyfer y 12 mis diwethaf dros £30,000 ar ôl TAW?" >> ../conf/messages.cy
echo "wasTurnoverMoreAfterVAT.heading = A oedd eich trosiant ar gyfer y 12 mis diwethaf dros £30,000 ar ôl TAW?" >> ../conf/messages.cy
echo "wasTurnoverMoreAfterVAT.Yes = Oedd - roedd fy nhrosiant dros £30,000" >> ../conf/messages.cy
echo "wasTurnoverMoreAfterVAT.No = Nac oedd - roedd fy nhrosiant yn llai na £30,000" >> ../conf/messages.cy
echo "wasTurnoverMoreAfterVAT.error.required = Dewis iawn neu na" >> ../conf/messages.cy

echo "Adding navigation default to NextPage Object"
echo "    with utils.nextpage.employer.cis.uk.subcontractor.WasTurnoverMoreAfterVATNextPage" >> ../app/utils/NextPage.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration WasTurnoverMoreAfterVAT completed"
