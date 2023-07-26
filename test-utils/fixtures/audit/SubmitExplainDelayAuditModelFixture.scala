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

package fixtures.audit

import models.audit.SubmitExplainDelayAudit
import models.response.emcsTfe.SubmitExplainDelayResponse
import models.submitExplainDelay.{SubmitExplainDelayModel, SubmitterType}
import models.{DelayReason, DelayType, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

object SubmitExplainDelayAuditModelFixture {
  val time = LocalDate.now().toString

  val submitExplainDelayAuditSuccessful: SubmitExplainDelayAudit = SubmitExplainDelayAudit(
    credentialId = "credentialId",
    internalId = "internalId",
    ern = "ERN1",
    submissionRequest = SubmitExplainDelayModel(
      arc = "ARC1",
      sequenceNumber = 1,
      submitterType = SubmitterType.Consignor,
      delayType = DelayType.ReportOfReceipt,
      delayReasonType = DelayReason.Other,
      additionalInformation = Some("more information")
    ),
    submissionResponse = Right(
      SubmitExplainDelayResponse(
        receipt = "1234567890",
        receiptDate = s"$time"
      )
    )
  )


  val submitExplainDelayAuditSuccessfulJSON: JsValue = Json.parse(
    s"""
       |{
       |   "credentialId": "credentialId",
       |   "internalId": "internalId",
       |   "ern": "ERN1",
       |   "submissionRequest": {
       |       "arc": "ARC1",
       |       "sequenceNumber": 1,
       |       "submitterType": "${SubmitterType.Consignor.toString}",
       |       "delayType": "${DelayType.ReportOfReceipt.toString}",
       |       "delayReasonType": "${DelayReason.Other.toString}",
       |       "additionalInformation": "more information"
       |   },
       |   "submissionResponse" : {
       |      "success": {
       |          "receipt": "1234567890",
       |          "receiptDate": "$time"
       |      }
       |   }
       |}
       |""".stripMargin)


  val submitExplainDelayAuditFailed: SubmitExplainDelayAudit = SubmitExplainDelayAudit(
    credentialId = "credentialId",
    internalId = "internalId",
    ern = "ERN1",
    submissionRequest = SubmitExplainDelayModel(
      arc = "ARC1",
      sequenceNumber = 1,
      submitterType = SubmitterType.Consignor,
      delayType = DelayType.ReportOfReceipt,
      delayReasonType = DelayReason.Other,
      additionalInformation = Some("more information")
    ),
    submissionResponse = Left(UnexpectedDownstreamResponseError)
  )

  val submitExplainDelayAuditFailedJSON: JsValue = Json.parse(
    s"""
       |{
       |   "credentialId": "credentialId",
       |   "internalId": "internalId",
       |   "ern": "ERN1",
       |   "submissionRequest": {
       |       "arc": "ARC1",
       |       "sequenceNumber": 1,
       |       "submitterType": "${SubmitterType.Consignor.toString}",
       |       "delayType": "${DelayType.ReportOfReceipt.toString}",
       |       "delayReasonType": "${DelayReason.Other.toString}",
       |       "additionalInformation": "more information"
       |   },
       |   "submissionResponse" : {
       |      "failure": "Unexpected downstream response status"
       |   }
       |}
       |""".stripMargin)
}
