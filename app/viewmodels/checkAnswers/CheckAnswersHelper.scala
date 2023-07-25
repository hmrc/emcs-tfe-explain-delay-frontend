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

package viewmodels.checkAnswers

import models.requests.DataRequest
import models.{ConfirmationDetails, UserAnswers}
import pages.{DelayDetailsPage, DelayReasonPage, DelayTypePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList

import javax.inject.Inject

class CheckAnswersHelper @Inject()(delayDetailsSummary: DelayDetailsSummary) {

  def summaryList(showActionLinks: Boolean = true)(implicit userAnswers: UserAnswers, messages: Messages): SummaryList =
    SummaryList(
      rows = Seq(
        DelayTypeSummary.row(showActionLinks),
        DelayReasonSummary.row(showActionLinks),
        delayDetailsSummary.row(showActionLinks)
      ).flatten
    )

  def summaryList(confirmationDetails: ConfirmationDetails)(implicit request: DataRequest[_], messages: Messages): SummaryList = {

    val userAnswersFromConfirmationDetails = UserAnswers(
      internalId = request.internalId,
      ern = request.ern,
      arc = request.arc
    )
      .set(DelayTypePage, confirmationDetails.delayType)
      .set(DelayReasonPage, confirmationDetails.delayReason)
      .set(DelayDetailsPage, confirmationDetails.delayDetails)

    summaryList(showActionLinks = false)(userAnswersFromConfirmationDetails, messages)

  }

}
