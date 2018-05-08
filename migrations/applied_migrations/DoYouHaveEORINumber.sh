#!/bin/bash

echo "Applying migration DoYouHaveEORINumber"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /other/import-export/nes                       controllers.other.importexports.nes.DoYouHaveEORINumberController.onPageLoad()" >> ../conf/app.routes

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration DoYouHaveEORINumber completed"
