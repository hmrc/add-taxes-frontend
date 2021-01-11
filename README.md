
# add-taxes-frontend

[![Build Status](https://travis-ci.org/hmrc/add-taxes-frontend.svg?branch=master)](https://travis-ci.org/hmrc/add-taxes-frontend) [ ![Download](https://api.bintray.com/packages/hmrc/releases/add-taxes-frontend/images/download.svg) ](https://bintray.com/hmrc/releases/add-taxes-frontend/_latestVersion)

The add taxes section of the Business tax account.
Many of its journeys are still in Business tax account and will be migrated in due course.  

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

### Endpoints

| Method | Path                                                                       | Description                                |
|--------|----------------------------------------------------------------------------|--------------------------------------------|
|  POST  | ```add-taxes-frontend.public.mdtp/internal/self-assessment/enrol-for-sa``` | Retrieve Url to redirect for SA enrolment  |


#### POST /add-taxes-frontend.public.mdtp/internal/self-assessment/enrol-for-sa

| Service| Origin  |
|--------|---------|
|  BTA   | bta-sa  |
|  PTA   | pta-sa  |
|  SSTTP | ssttp-sa|

| Fields |  Description(Optional or Mandatory) |
|--------|-----------|
| origin | Mandatory |
|  utr   | Optional  |

**Request format**
```
{
  "origin": "bta-sa", // your origin here
  "utr": "1234567890"
}
```

**Response format**
```
{
  "redirectUrl": "/example-url" // relative url
}
```

##### Response with
| Status | Result                                             |
|:------:|----------------------------------------------------|
|   200  | Returns Json                                       |
|   400  | Return error message "Invalid origin: test-origin" |
|   400  | Return error message "Invalid utr: test-utr"                              |
|   400  | Return error message "Invalid origin: test-origin, Invalid utr: test-utr" |
|   400  | Return error message "Json Body is incorrect"                             |