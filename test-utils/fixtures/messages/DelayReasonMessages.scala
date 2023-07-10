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

package fixtures.messages

import fixtures.i18n


object DelayReasonMessages {

  sealed trait ViewMessages { _: i18n =>
    def heading(delayType: String): String
    def title(delayType: String): String
    val reportReceiptDelay: String
    val changeOfDestinationDelay: String
    val radioOther: String
    val radioCancelled: String
    val radioPending: String
    val radioInvestigation: String
    val radioWeather: String
    val radioStrikes: String
    val radioAccident: String
    val cyaLabel: String
    val cyaChangeHidden: String
  }
  object English extends ViewMessages with BaseEnglish {
    override def heading(delayType: String): String = s"Why has $delayType been delayed?"
    override def title(delayType: String): String = titleHelper(heading(delayType))
    override val reportReceiptDelay: String = "submitting a report of receipt"
    override val changeOfDestinationDelay: String = "providing a consignee or change of destination for the movement"
    override val radioOther: String = "Other"
    override val radioCancelled: String = "The commercial transaction has been cancelled"
    override val radioPending: String = "The commercial transaction is pending"
    override val radioInvestigation: String = "Ongoing investigation by officials"
    override val radioWeather: String = "Bad weather conditions"
    override val radioStrikes: String = "Strike action"
    override val radioAccident: String = "An accident in transit"
    override val cyaLabel: String = "Reason for delay"
    override val cyaChangeHidden: String = "reason for delay"
  }
  object Welsh extends ViewMessages with BaseWelsh {
    override def heading(delayType: String): String = s"Why has $delayType been delayed?"
    override def title(delayType: String): String = titleHelper(heading(delayType))
    override val reportReceiptDelay: String = "submitting a report of receipt"
    override val changeOfDestinationDelay: String = "providing a consignee or change of destination for the movement"
    override val radioOther: String = "Other"
    override val radioCancelled: String = "The commercial transaction has been cancelled"
    override val radioPending: String = "The commercial transaction is pending"
    override val radioInvestigation: String = "Ongoing investigation by officials"
    override val radioWeather: String = "Bad weather conditions"
    override val radioStrikes: String = "Strike action"
    override val radioAccident: String = "An accident in transit"
    override val cyaLabel: String = "Reason for delay"
    override val cyaChangeHidden: String = "reason for delay"
  }

}
