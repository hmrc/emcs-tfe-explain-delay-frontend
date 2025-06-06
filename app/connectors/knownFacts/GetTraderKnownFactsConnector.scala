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

import config.AppConfig
import models.{ErrorResponse, JsonValidationError, TraderKnownFacts, UnexpectedDownstreamResponseError}
import play.api.libs.json.JsResultException
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetTraderKnownFactsConnector @Inject()(val http: HttpClientV2,
                                             config: AppConfig) extends GetTraderKnownFactsHttpParser {

  def baseUrl: String = config.traderKnownFactsBaseUrl

  def getTraderKnownFacts(ern: String)
                         (implicit headerCarrier: HeaderCarrier,
                          executionContext: ExecutionContext): Future[Either[ErrorResponse, Option[TraderKnownFacts]]] =
    get(baseUrl, ern)
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getTraderKnownFacts] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getTraderKnownFacts] Unexpected error from emcs-tfe: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
}
