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


object DelayTypeMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val h1: String
    val p1: String
    val p2: String
    val delayTypeLegend: String
    val radioOption1: String
    val radioOption2: String
    val cyaLabel: String
    val cyaChangeHidden: String
  }
  object English extends ViewMessages with BaseEnglish {
    override val heading = "What’s been delayed?"
    override val title: String = titleHelper(heading)
    override val h1 = "Tell HMRC about a delay to provide information about a movement"
    override val p1 = "This explanation for delay will be sent to HMRC only. It will not change the expected delivery time for any goods still in transit."
    override val p2 = "If you are the consignee, you will still need to submit a report of receipt for this movement."
    override val delayTypeLegend: String = "What’s been delayed?"
    override val radioOption1: String = "Submitting a report of receipt"
    override val radioOption2: String = "Providing a consignee or change of destination for the movement"
    override val cyaLabel: String = "What’s been delayed"
    override val cyaChangeHidden: String = "what’s been delayed"
  }
  object Welsh extends ViewMessages with BaseWelsh {
    override val heading = "What’s been delayed?"
    override val title: String = titleHelper(heading)
    override val h1 = "Tell HMRC about a delay to provide information about a movement"
    override val p1 = "This explanation for delay will be sent to HMRC only. It will not change the expected delivery time for any goods still in transit."
    override val p2 = "If you are the consignee, you will still need to submit a report of receipt for this movement."
    override val delayTypeLegend: String = "What’s been delayed?"
    override val radioOption1: String = "Submitting a report of receipt"
    override val radioOption2: String = "Providing a consignee or change of destination for the movement"
    override val cyaLabel: String = "What’s been delayed"
    override val cyaChangeHidden: String = "what’s been delayed"
  }

}
