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
import models.{ConfirmationDetails, NormalMode}
import navigation.Navigator
import pages.CheckYourAnswersPage
import play.api.i18n.MessagesApi
import play.api.libs.json.Format.GenericFormat
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewmodels.checkAnswers.{DelayDetailsSummary, DelayReasonSummary, DelayTypeSummary}
import views.html.CheckYourAnswersView

import javax.inject.Inject
import scala.concurrent.Future

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            override val userAnswersService: UserAnswersService,
                                            override val navigator: Navigator,
                                            override val auth: AuthAction,
                                            override val withMovement: MovementAction,
                                            override val getData: DataRetrievalAction,
                                            override val requireData: DataRequiredAction,
                                            override val userAllowList: UserAllowListAction,
                                            val delayDetailsSummary: DelayDetailsSummary,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>

      Future.successful(Ok(view(
        SummaryList(
          Seq(
            DelayTypeSummary.row(request.userAnswers),
            DelayReasonSummary.row(request.userAnswers),
            Some(delayDetailsSummary.row(request.userAnswers))
          ).flatten
        ),
        routes.CheckYourAnswersController.onSubmit(ern, arc)
      )))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      val reference = "UYVQBLMXCYK6HAEBZI7TSWAQ6XDTXFYU" //TODO: Create submission model and send to BE, then handle response
      saveAndRedirect(CheckYourAnswersPage, ConfirmationDetails(reference), NormalMode)
    }
}
