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

import controllers.actions.{AuthAction, DataRetrievalAction, MovementAction}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import views.html.IndexPage

import javax.inject.Inject
import scala.concurrent.Future

class IndexController @Inject()(override val messagesApi: MessagesApi,
                                val userAnswersService: UserAnswersService,
                                val controllerComponents: MessagesControllerComponents,
                                authAction: AuthAction,
                                withMovement: MovementAction,
                                getData: DataRetrievalAction,
                                view: IndexPage
                               ) extends BaseController {

  def onPageLoad(ern: String, arc: String): Action[AnyContent] =
    (authAction(ern, arc) andThen withMovement.fromCache(arc) andThen getData).async { implicit request =>
      request.userAnswers match {
        case _ => Future.successful(Ok(view()))
      }
    }


}
