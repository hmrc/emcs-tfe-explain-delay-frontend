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

package navigation

import base.SpecBase
import controllers.routes
import models.DelayReason.{Other, Strikes}
import models._
import pages._

class NavigatorSpec extends SpecBase {

  val navigator = new Navigator

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {
        case object UnknownPage extends Page

        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.IndexController.onPageLoad(testErn, testArc)
      }

      "for the DelayType page" - {

        "must go to UnderConstruction page" in {
          navigator.nextPage(DelayTypePage, NormalMode, emptyUserAnswers) mustBe
            routes.DelayReasonController.onPageLoad(testErn, testArc, NormalMode)
        }
      }

      "for the DelayReason page" - {

        "when reason is `Other`" - {

          "must go to DelayDetails page" in {
            navigator.nextPage(DelayReasonPage, NormalMode, emptyUserAnswers.set(DelayReasonPage, Other)) mustBe
              routes.DelayDetailsController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "when reason is anything other than `Other`" - {

          "must go to DelayDetailsChoice page" in {
            navigator.nextPage(DelayReasonPage, NormalMode, emptyUserAnswers.set(DelayReasonPage, Strikes)) mustBe
              routes.DelayDetailsChoiceController.onPageLoad(testErn, testArc, NormalMode)
          }
        }
      }

      "for the DelayDetailsChoicePage page" - {

        "when answer is 'Yes' (true)" - {

          "must go to DelayDetails page" in {
            navigator.nextPage(DelayDetailsChoicePage, NormalMode, emptyUserAnswers.set(DelayDetailsChoicePage, true)) mustBe
              routes.DelayDetailsController.onPageLoad(testErn, testArc, NormalMode)
          }
        }

        "when answer is 'No' (false)" - {

          "must go to CheckYouAnswers page" in {
            navigator.nextPage(DelayDetailsChoicePage, NormalMode, emptyUserAnswers.set(DelayDetailsChoicePage, false)) mustBe
              routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "for the DelayDetailsPage page" - {

        "must go to CheckYourAnswers page" in {
          navigator.nextPage(DelayDetailsPage, NormalMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }

      "for the CheckYourAnswers page" - {

        "must go to Confirmation page" in {
          navigator.nextPage(CheckYourAnswersPage, NormalMode, emptyUserAnswers) mustBe
            routes.ConfirmationController.onPageLoad(testErn, testArc)
        }
      }
    }

    "in Check mode" - {

      "DelayReasonPage" - {

        "when the answer is Other" - {

          "must go to the CheckYouAnswers page for any page" in {

            navigator.nextPage(DelayReasonPage, CheckMode, emptyUserAnswers.set(DelayReasonPage, Other)) mustBe
              routes.DelayDetailsController.onPageLoad(testErn, testArc, CheckMode)
          }
        }

        "when the answer is NOT Other" - {

          "must go to the CheckYouAnswers page for any page" in {

            navigator.nextPage(DelayReasonPage, CheckMode, emptyUserAnswers.set(DelayReasonPage, Strikes)) mustBe
              routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
          }
        }
      }

      "Other Pages" - {

        "must go to the CheckYouAnswers page for any page" in {

          navigator.nextPage(DelayTypePage, CheckMode, emptyUserAnswers) mustBe
            routes.CheckYourAnswersController.onPageLoad(testErn, testArc)
        }
      }
    }
  }
}
