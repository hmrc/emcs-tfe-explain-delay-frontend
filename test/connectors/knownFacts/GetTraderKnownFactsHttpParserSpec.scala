/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors.knownFacts

import base.SpecBase
import mocks.MockHttpClient
import models.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.HttpResponse

class GetTraderKnownFactsHttpParserSpec extends SpecBase with GetTraderKnownFactsHttpParser with MockHttpClient {

  override def http: HttpClientV2 = mockHttpClient

  "GetTraderKnownFactsReads.read(method: String, url: String, response: HttpResponse)" - {

    "should return 'Some(traderKnownFacts)'" - {
      s"when an OK (${Status.OK}) response is retrieved and JSON is valid" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.OK, Json.obj("traderName" -> "testTraderName").toString())) mustBe Right(Some(testMinTraderKnownFacts))
      }
    }

    "should return JsonValidationError" - {
      s"when an OK (${Status.OK}) response is retrieved but JSON is invalid" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.OK, "bad json")) mustBe Left(JsonValidationError)
      }
    }

    "should return 'None'" - {
      s"when a NOT_FOUND (${Status.NO_CONTENT}) response is retrieved" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.NO_CONTENT, "")) mustBe Right(None)
      }
    }

    "should return UnexpectedDownstreamError" - {
      s"when status is anything else" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, "")) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
