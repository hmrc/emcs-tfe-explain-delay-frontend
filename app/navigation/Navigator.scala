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

import controllers.routes
import models.DelayReason.Other
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

import javax.inject.Inject

class Navigator @Inject()() extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case DelayTypePage => (userAnswers: UserAnswers) =>
      routes.DelayReasonController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
    case DelayReasonPage => (userAnswers: UserAnswers) =>
      userAnswers.get(DelayReasonPage) match {
        case Some(Other) =>
          routes.DelayDetailsController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          routes.DelayDetailsChoiceController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
      }
    case DelayDetailsChoicePage => (userAnswers: UserAnswers) =>
      userAnswers.get(DelayDetailsChoicePage) match {
        case Some(true) =>
          routes.DelayDetailsController.onPageLoad(userAnswers.ern, userAnswers.arc, NormalMode)
        case _ =>
          routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
      }
    case DelayDetailsPage => (userAnswers: UserAnswers) =>
      routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case CheckYourAnswersPage => (userAnswers: UserAnswers) =>
      routes.ConfirmationController.onPageLoad(userAnswers.ern, userAnswers.arc)
    case _ => (userAnswers: UserAnswers) =>
      routes.IndexController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }

  private val checkRoutes: Page => UserAnswers => Call = {
    case DelayReasonPage => (userAnswers: UserAnswers) => delayReasonCheckRouting(userAnswers)
    case _ => (userAnswers: UserAnswers) =>
      routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
  }


  def delayReasonCheckRouting(userAnswers: UserAnswers): Call = {
    userAnswers.get(DelayReasonPage) match {
      case Some(Other) => routes.DelayDetailsController.onPageLoad(userAnswers.ern, userAnswers.arc, CheckMode)
      case _ => routes.CheckYourAnswersController.onPageLoad(userAnswers.ern, userAnswers.arc)
    }
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode => normalRoutes(page)(userAnswers)
    case _ => checkRoutes(page)(userAnswers)
  }
}
