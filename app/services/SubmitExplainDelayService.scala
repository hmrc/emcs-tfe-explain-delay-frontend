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

package services

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


import config.AppConfig
import connectors.emcsTfe.SubmitExplainDelayConnector
import featureswitch.core.config.{EnableNRS, FeatureSwitching}
import models.audit.SubmitExplainDelayAudit
import models.requests.DataRequest
import models.response.emcsTfe.SubmitExplainDelayResponse
import models.submitExplainDelay.SubmitExplainDelayModel
import models.{ErrorResponse, SubmitExplainDelayException}
import services.nrs.NRSBrokerService
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitExplainDelayService @Inject()(submitExplainDelayConnector: SubmitExplainDelayConnector,
                                          nrsBrokerService: NRSBrokerService,
                                          auditingService: AuditingService,
                                          override val config: AppConfig)
                                         (implicit ec: ExecutionContext) extends Logging with FeatureSwitching {

  def submit(ern: String, arc: String)(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Future[SubmitExplainDelayResponse] = {

    val submissionRequest = SubmitExplainDelayModel(dataRequest.movementDetails)(dataRequest.userAnswers)

    if(isEnabled(EnableNRS)) {
      nrsBrokerService.submitPayload(submissionRequest, ern).flatMap(_ => handleSubmission(ern, arc, submissionRequest))
    } else {
      handleSubmission(ern, arc, submissionRequest)
    }
  }

  private def handleSubmission(ern: String, arc: String, submissionRequest: SubmitExplainDelayModel)
                    (implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Future[SubmitExplainDelayResponse] =
    submitExplainDelayConnector.submit(ern, submissionRequest).map {
      case Right(submissionResponse) =>
        logger.info("[submit] Successful explain a delay submission")
        writeAudit(submissionRequest, Right(submissionResponse))
        submissionResponse
      case Left(errorResponse) =>
        logger.warn("[submit] Unsuccessful explain a delay submission")
        writeAudit(submissionRequest, Left(errorResponse))
        throw SubmitExplainDelayException(s"Failed to submit Explain a Delay to emcs-tfe for ern: '$ern' & arc: '$arc'")
    }

  private def writeAudit(
                          submissionRequest: SubmitExplainDelayModel,
                          submissionResponse: Either[ErrorResponse, SubmitExplainDelayResponse]
                        )(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Unit = {

    auditingService.audit(
      SubmitExplainDelayAudit(
        credentialId = dataRequest.request.request.credId,
        internalId = dataRequest.internalId,
        ern = dataRequest.ern,
        receiptDate = LocalDateTime.now.toString,
        submissionRequest = submissionRequest,
        submissionResponse = submissionResponse
      )
    )
  }

}
