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
import handlers.ErrorHandler
import models.requests.DataRequest
import models.response.emcsTfe.SubmitExplainDelayResponse
import models.{ConfirmationDetails, MissingMandatoryPage, NormalMode, UserAnswers}
import navigation.Navigator
import pages._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{SubmitExplainDelayService, UserAnswersService}
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsonOptionFormatter.optionFormat
import viewmodels.checkAnswers.DelayDetailsSummary
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
                                            val submitExplainDelayService: SubmitExplainDelayService,
                                            val delayDetailsSummary: DelayDetailsSummary,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            errorHandler: ErrorHandler
                                          ) extends BaseNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithCachedMovementAsync(ern, arc) { implicit request =>
      Future.successful(Ok(view(routes.CheckYourAnswersController.onSubmit(ern, arc))))
    }

  def onSubmit(ern: String, arc: String): Action[AnyContent] =
    authorisedDataRequestWithUpToDateMovementAsync(ern, arc) { implicit request =>
      submitExplainDelayService.submit(ern, arc).flatMap { response =>

        deleteDraftAndSetConfirmationFlow(request.internalId, request.ern, request.arc, response).map { _ =>
          Redirect(navigator.nextPage(CheckYourAnswersPage, NormalMode, request.userAnswers))
        }

      } recover {
        case _: MissingMandatoryPage =>
          BadRequest(errorHandler.badRequestTemplate)
        case _ =>
          InternalServerError(errorHandler.internalServerErrorTemplate)
      }
    }

  private def deleteDraftAndSetConfirmationFlow(internalId: String,
                                                ern: String,
                                                arc: String,
                                                response: SubmitExplainDelayResponse)
                                               (implicit hc: HeaderCarrier, request: DataRequest[_]): Future[UserAnswers] = {
    userAnswersService.set(
      UserAnswers(
        internalId,
        ern,
        arc,
        data = Json.obj(
          ConfirmationPage.toString ->
            ConfirmationDetails(
              receipt = response.receipt,
              delayType = request.userAnswers.get(DelayTypePage).get,
              delayReason = request.userAnswers.get(DelayReasonPage).get,
              delayDetails = request.userAnswers.get(DelayDetailsPage).flatten
            )
        )
      )
    )
  }
}


