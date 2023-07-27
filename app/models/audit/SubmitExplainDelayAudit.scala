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

package models.audit

import models.ErrorResponse
import models.response.emcsTfe.SubmitExplainDelayResponse
import models.submitExplainDelay.SubmitExplainDelayModel
import play.api.libs.json.{JsValue, Json}

case class SubmitExplainDelayAudit(
                                    credentialId: String,
                                    internalId: String,
                                    ern: String,
                                    submissionRequest: SubmitExplainDelayModel,
                                    submissionResponse: Either[ErrorResponse, SubmitExplainDelayResponse]
                                  ) extends AuditModel {

  override val auditType: String = "explainDelaySubmission"

  override val detail: JsValue = Json.obj(fields =
    "credentialId" -> credentialId,
    "internalId" -> internalId,
    "ern" -> ern,
    "arc" -> submissionRequest.arc,
    "sequenceNumber" -> submissionRequest.sequenceNumber,
    "submitterType" -> submissionRequest.submitterType,
    "delayType" -> submissionRequest.delayType,
    "delayReasonType" -> submissionRequest.delayReasonType,
    "additionalInformation" -> submissionRequest.additionalInformation
  ) ++ {
    submissionResponse match {
      case Right(success) =>
        Json.obj(fields =
          "status" -> "success",
          "receipt" -> success.receipt,
          "receiptDate" -> success.receiptDate
        )
      case Left(failedMessage) =>
        Json.obj(fields =
          "status" -> "failed",
          "failedMessage" -> failedMessage.message
        )
    }
  }
}