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

package models.submitExplainDelay

import models.response.emcsTfe.GetMovementResponse
import models.{DelayReason, DelayType, UserAnswers}
import pages.{DelayDetailsPage, DelayReasonPage, DelayTypePage}
import play.api.libs.json.{Format, Json}
import utils.{JsonOptionFormatter, ModelConstructorHelpers}

case class SubmitExplainDelayModel(arc: String,
                                   sequenceNumber: Int,
                                   submitterType: SubmitterType,
                                   delayType: DelayType,
                                   delayReasonType: DelayReason,
                                   additionalInformation: Option[String])

object SubmitExplainDelayModel extends JsonOptionFormatter with ModelConstructorHelpers {

  implicit val fmt: Format[SubmitExplainDelayModel] = Json.format

  def apply(movementDetails: GetMovementResponse)(implicit userAnswers: UserAnswers): SubmitExplainDelayModel = {

    val submitter: SubmitterType = {
      if (movementDetails.consignorTrader.traderExciseNumber.contains(userAnswers.ern)) SubmitterType.Consignor else SubmitterType.Consignee
    }

    SubmitExplainDelayModel(
      arc = movementDetails.arc,
      submitterType = submitter,
      sequenceNumber = movementDetails.sequenceNumber,
      delayType = mandatoryPage(DelayTypePage),
      delayReasonType = mandatoryPage(DelayReasonPage),
      additionalInformation = userAnswers.get(DelayDetailsPage).flatten
    )

  }
}
