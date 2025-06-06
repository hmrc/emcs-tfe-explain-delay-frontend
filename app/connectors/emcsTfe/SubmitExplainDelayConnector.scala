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

package connectors.emcsTfe

import config.AppConfig
import models.response.emcsTfe.SubmitExplainDelayResponse
import models.submitExplainDelay.SubmitExplainDelayModel
import models.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitExplainDelayConnector @Inject()(val http: HttpClientV2,
                                            config: AppConfig) extends EmcsTfeHttpParser[SubmitExplainDelayResponse] {

  override implicit val reads: Reads[SubmitExplainDelayResponse] = SubmitExplainDelayResponse.reads

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def submit(exciseRegistrationNumber: String, submitExplainDelayModel: SubmitExplainDelayModel)
            (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, SubmitExplainDelayResponse]] = {
    post(url"$baseUrl/explain-delay/$exciseRegistrationNumber/${submitExplainDelayModel.arc}", submitExplainDelayModel)

  }.recover {
    ex =>
      logger.warn(s"[submit] Unexpected response from emcs-tfe : ${ex.getMessage}")
      Left(UnexpectedDownstreamResponseError)
  }

}
