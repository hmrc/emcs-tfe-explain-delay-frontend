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
import config.AppConfig
import mocks.MockHttpClient
import models.UnexpectedDownstreamResponseError
import org.scalatest.BeforeAndAfterAll
import play.api.Application
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class GetTraderKnownFactsConnectorSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with BeforeAndAfterAll {

  lazy val app: Application = applicationBuilder(userAnswers = None).build()

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  lazy val connector = new GetTraderKnownFactsConnector(mockHttpClient, appConfig)

  "getTraderKnownFacts" - {
    "should return a successful response" - {
      "when downstream call is successful" in {

        MockHttpClient.get(
          url"${appConfig.traderKnownFactsBaseUrl}?exciseRegistrationId=$testErn"
        ).returns(Future.successful(Right(Some(testMinTraderKnownFacts))))

        connector.getTraderKnownFacts(testErn).futureValue mustBe Right(Some(testMinTraderKnownFacts))
      }
    }

    "should return an error response" - {
      "when downstream call fails" in {
        MockHttpClient.get(
          url"${appConfig.traderKnownFactsBaseUrl}?exciseRegistrationId=$testErn"
        ).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
