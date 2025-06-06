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

package controllers.action

import base.SpecBase
import controllers.actions.DataRequiredActionImpl
import controllers.routes
import mocks.services.MockUserAnswersService
import models.DelayReason.Strikes
import models.DelayType.ReportOfReceipt
import models.requests.{DataRequest, MovementRequest, OptionalDataRequest, UserRequest}
import models.{ConfirmationDetails, NormalMode}
import org.scalatestplus.mockito.MockitoSugar
import pages.{ConfirmationPage, DelayReasonPage}
import play.api.http.Status.SEE_OTHER
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.LOCATION

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.reflectiveCalls

class DataRequiredActionSpec extends SpecBase with MockitoSugar with MockUserAnswersService {

  lazy val dataRequiredAction = new DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  val fakeRequest = FakeRequest("GET", "/")

  "DataRequiredAction" - {

    "when there are no user answers saved" - {

      "must redirect to the JourneyRecoveryController" in {

        val result = dataRequiredAction.callRefine(
          OptionalDataRequest(
            MovementRequest(
              UserRequest(fakeRequest, testErn, testInternalId, testCredId, false),
              testArc,
              getMovementResponseModel
            ),
            None,
            None
          )
        ).futureValue

        result.isLeft mustBe true
        result.left.map(
          left => {
            left.header.status mustBe SEE_OTHER
            left.header.headers.get(LOCATION) mustBe Some(routes.JourneyRecoveryController.onPageLoad(testErn, testArc).url)
          }
        )
      }

    }


    "when there are confirmation page details saved" - {

      "and the current page is not the confirmation page" - {

        "must redirect to the not permitted page" in {

          val userAnswers = emptyUserAnswers.set(ConfirmationPage, ConfirmationDetails(
            receipt = testConfirmationReference,
            delayType = ReportOfReceipt,
            delayReason = Strikes,
            delayDetails = None
          ))

          val result = dataRequiredAction.callRefine(
            OptionalDataRequest(
              MovementRequest(
                UserRequest(fakeRequest, testErn, testInternalId, testCredId, false),
                testArc,
                getMovementResponseModel
              ),
              Some(userAnswers),
              None
            )
          ).futureValue

          result.isLeft mustBe true
          result.left.map(
            left => {
              left.header.status mustBe SEE_OTHER
              left.header.headers.get(LOCATION) mustBe Some(routes.NotPermittedPageController.onPageLoad(testErn, testArc).url)
            }
          )
        }

      }

      "and the current page is the confirmation page" - {

        "must return a Right(DataRequest)" in {

          val fakeRequest = FakeRequest("GET", routes.ConfirmationController.onPageLoad(testErn, testArc).url)

          val userAnswers = emptyUserAnswers.set(ConfirmationPage, ConfirmationDetails(
            receipt = testConfirmationReference,
            delayType = ReportOfReceipt,
            delayReason = Strikes,
            delayDetails = None
          ))
          val traderKnownFacts = testMinTraderKnownFacts

          val result = dataRequiredAction.callRefine(
            OptionalDataRequest(
              MovementRequest(
                UserRequest(fakeRequest, testErn, testInternalId, testCredId, false),
                testArc,
                getMovementResponseModel
              ),
              Some(userAnswers),
              Some(traderKnownFacts)
            )
          ).futureValue

          result.isRight mustBe true
        }
      }

    }

    "when there are user answers saved" - {

      "must return a Right(DataRequest)" in {

        val fakeRequest = FakeRequest("GET", routes.DelayReasonController.onPageLoad(testErn, testArc, NormalMode).url)

        val userAnswers = emptyUserAnswers.set(DelayReasonPage, Strikes)
        val traderKnownFacts = testMinTraderKnownFacts

        val result = dataRequiredAction.callRefine(
          OptionalDataRequest(
            MovementRequest(
              UserRequest(fakeRequest, testErn, testInternalId, testCredId, false),
              testArc,
              getMovementResponseModel
            ),
            Some(userAnswers),
            Some(traderKnownFacts)
          )
        ).futureValue

        result.isRight mustBe true
      }

    }


  }
}
