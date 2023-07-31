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

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.common.TraderModel
import models.{DelayReason, DelayType, UserAnswers}
import pages.{DelayDetailsPage, DelayReasonPage, DelayTypePage}

class SubmitExplainDelayModelSpec extends SpecBase with GetMovementResponseFixtures {

  "SubmitExplainDelayModel" - {

    val consignorsERN = "GB000011111111"

    val consigneesERN = "GB000022222222"

    val movementResponseModel = getMovementResponseModel.copy(
      consignorTrader = TraderModel(
        traderExciseNumber = Some(consignorsERN),
        traderName = Some("MyConsignor"),
        address = Some(AddressModel(
          streetNumber = None,
          street = Some("Main101"),
          postcode = Some("ZZ78"),
          city = Some("Zeebrugge")
        )),
        eoriNumber = None
      )
    )

    val userAnswers = UserAnswers("internalId", "ern", testArc)
      .set(DelayTypePage, DelayType.ReportOfReceipt)
      .set(DelayReasonPage, DelayReason.Other)
      .set(DelayDetailsPage, Some("more information"))

    "sets the submitter type to consignor" in {
      val loggedInUserAnswers = userAnswers.copy(ern = consignorsERN)

      val submitExplainDelayModel = SubmitExplainDelayModel.apply(movementResponseModel)(loggedInUserAnswers)

      assert(submitExplainDelayModel.arc == testArc)
      assert(submitExplainDelayModel.delayType == DelayType.ReportOfReceipt)
      assert(submitExplainDelayModel.delayReasonType == DelayReason.Other)
      assert(submitExplainDelayModel.additionalInformation.contains("more information"))
      assert(submitExplainDelayModel.submitterType == SubmitterType.Consignor)
    }

    "sets the submitter type to consignee" in {
      val loggedInUserAnswers = userAnswers.copy(ern = consigneesERN)

      val submitExplainDelayModel = SubmitExplainDelayModel.apply(movementResponseModel)(loggedInUserAnswers)

      assert(submitExplainDelayModel.arc == testArc)
      assert(submitExplainDelayModel.delayType == DelayType.ReportOfReceipt)
      assert(submitExplainDelayModel.delayReasonType == DelayReason.Other)
      assert(submitExplainDelayModel.additionalInformation.contains("more information"))
      assert(submitExplainDelayModel.submitterType == SubmitterType.Consignee)
    }

  }

}
