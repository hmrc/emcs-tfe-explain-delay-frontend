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

package controllers

import controllers.actions._
import forms.DelayDetailsFormProvider
import models.{DelayReason, Mode}
import navigation.Navigator
import pages.{DelayDetailsPage, DelayReasonPage}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import utils.JsonOptionFormatter
import views.html.DelayDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class DelayDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        override val userAnswersService: UserAnswersService,
                                        override val navigator: Navigator,
                                        override val auth: AuthAction,
                                        override val withMovement: MovementAction,
                                        override val getData: DataRetrievalAction,
                                        override val requireData: DataRequiredAction,
                                        override val userAllowList: UserAllowListAction,
                                        formProvider: DelayDetailsFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: DelayDetailsView
                                      ) extends BaseNavigationController with AuthActionHelper with JsonOptionFormatter {


  def onPageLoad(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(DelayReasonPage) { delayReason =>
        val isOtherDelayReason = delayReason == DelayReason.Other

        Future.successful(
          Ok(view(fillForm(DelayDetailsPage, formProvider(isOtherDelayReason)), isOtherDelayReason, mode))
        )
      }
    }

  def onSubmit(ern: String, arc: String, mode: Mode): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      withAnswer(DelayReasonPage) { delayReason =>

        val isOtherDelayReason = delayReason == DelayReason.Other

        formProvider(isOtherDelayReason).bindFromRequest().fold(
          formWithErrors =>
            Future.successful(
              BadRequest(view(formWithErrors, isOtherDelayReason, mode))
            ),
          value =>
            saveAndRedirect(DelayDetailsPage, value, mode)
        )

      }
    }
}
