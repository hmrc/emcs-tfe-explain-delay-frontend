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


object ConfirmationMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val delayInformationH2: String
    val print: String
    val whatNextH2: String
    val whatNextP1: String
    val mayNeedToDoH2: String
    val mayNeedToDoP1: String
    val mayNeedToDoP2: String
    val atAGlanceLink: String
    val feedback: String
  }
  object English extends ViewMessages with BaseEnglish {
    val heading: String = "Explanation for delay submitted"
    val title: String = titleHelper(heading)
    val delayInformationH2: String = "Delay information"
    val print: String = "Print this screen to make a record of your submission."
    val whatNextH2: String = "What happens next"
    val whatNextP1: String = "The movement will be updated to show you have successfully submitted an explanation for delay. This may not happen straight away."
    val mayNeedToDoH2: String = "What you may need to do"
    val mayNeedToDoP1: String = "This explanation for delay has been submitted to HMRC only. You may also need to notify the consignor or consignee that you have done this."
    val mayNeedToDoP2: String = "If you are the consignee you will still need to submit a report of receipt for this movement."
    val atAGlanceLink: String = "Return to at a glance"
    val feedback: String = "What did you think of this service? (opens in new tab) (takes 30 seconds)"
  }
  object Welsh extends ViewMessages with BaseWelsh {
    val heading: String = "Explanation for delay submitted"
    val title: String = titleHelper(heading)
    val delayInformationH2: String = "Delay information"
    val print: String = "Print this screen to make a record of your submission."
    val whatNextH2: String = "What happens next"
    val whatNextP1: String = "The movement will be updated to show you have successfully submitted an explanation for delay. This may not happen straight away."
    val mayNeedToDoH2: String = "What you may need to do"
    val mayNeedToDoP1: String = "This explanation for delay has been submitted to HMRC only. You may also need to notify the consignor or consignee that you have done this."
    val mayNeedToDoP2: String = "If you are the consignee you will still need to submit a report of receipt for this movement."
    val atAGlanceLink: String = "Return to at a glance"
    val feedback: String = "What did you think of this service? (opens in new tab) (takes 30 seconds)"
  }

}
