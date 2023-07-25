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

package fixtures

import models.response.emcsTfe.SubmitExplainDelayResponse
import models.submitExplainDelay.{SubmitExplainDelayModel, SubmitterType}
import models.{DelayReason, DelayType}
import play.api.libs.json.Json

trait SubmitExplainDelayFixtures extends BaseFixtures {

  val explainDelayModel = SubmitExplainDelayModel(
    arc = testArc,
    submitterType = SubmitterType.Consignor,
    sequenceNumber = 1,
    delayType = DelayType.ReportOfReceipt,
    delayReasonType = DelayReason.Other,
    additionalInformation = Some("more information about explaining the delay")
  )

  val successResponse = SubmitExplainDelayResponse(receipt = testConfirmationReference, receiptDate = testReceiptDate)

  val successResponseJson = Json.obj("receipt" -> testConfirmationReference, "receiptDate" -> testReceiptDate)
}
