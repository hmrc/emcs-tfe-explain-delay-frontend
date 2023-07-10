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


object DelayDetailsChoiceMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String
    val title: String
    val errorRequired: String
  }
  object English extends ViewMessages with BaseEnglish {
    val heading: String = "Do you want to give any more information about the delay?"
    val title: String = titleHelper(heading)
    val errorRequired: String = "Select yes if you want to give more information about the delay"
  }
  object Welsh extends ViewMessages with BaseWelsh {
    val heading: String = "Do you want to give any more information about the delay?"
    val title: String = titleHelper(heading)
    val errorRequired: String = "Select yes if you want to give more information about the delay"
  }

}
