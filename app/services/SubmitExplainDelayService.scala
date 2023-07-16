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


import connectors.emcsTfe.SubmitExplainDelayConnector
import models.SubmitExplainDelayException
import models.audit.{SubmitExplainDelayAuditModel, SubmitExplainDelayResponseAuditModel}
import models.requests.DataRequest
import models.response.emcsTfe.SubmitExplainDelayResponse
import models.submitExplainDelay.SubmitExplainDelayModel
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitExplainDelayService @Inject()(submitExplainDelayConnector: SubmitExplainDelayConnector,
                                          auditingService: AuditingService)
                                         (implicit ec: ExecutionContext) extends Logging {

  def submit(ern: String, arc: String)(implicit hc: HeaderCarrier, dataRequest: DataRequest[_]): Future[SubmitExplainDelayResponse] = {

    val submission = SubmitExplainDelayModel(dataRequest.movementDetails)(dataRequest.userAnswers)

    val auditCorrelationId = UUID.randomUUID().toString

    auditingService.audit(
      SubmitExplainDelayAuditModel(
        credentialId = dataRequest.request.request.credId,
        internalId = dataRequest.internalId,
        correlationId = auditCorrelationId,
        submission = submission,
        ern = ern
      )
    )

    submitExplainDelayConnector.submit(ern, submission).map {
      case Right(success) =>
        logger.info("[submit] Successful explain a delay submission")

        auditingService.audit(
          SubmitExplainDelayResponseAuditModel(
            credentialId = dataRequest.request.request.credId,
            internalId = dataRequest.internalId,
            correlationId = auditCorrelationId,
            arc = dataRequest.arc,
            traderId = ern,
            receipt = success.receipt
          )
        )
        success
      case Left(_) =>
        logger.warn("[submit] Unsuccessful explain a delay submission")

        throw SubmitExplainDelayException(s"Failed to submit Explain a Delay to emcs-tfe for ern: '$ern' & arc: '$arc'")
    }
  }
}
