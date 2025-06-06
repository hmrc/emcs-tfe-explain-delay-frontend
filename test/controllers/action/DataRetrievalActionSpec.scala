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
import controllers.actions.DataRetrievalActionImpl
import mocks.services.{MockGetTraderKnownFactsService, MockUserAnswersService}
import models.requests.{MovementRequest, OptionalDataRequest, UserRequest}
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.reflectiveCalls

class DataRetrievalActionSpec extends SpecBase with MockitoSugar with MockUserAnswersService with MockGetTraderKnownFactsService {

  lazy val dataRetrievalAction = new DataRetrievalActionImpl(mockUserAnswersService, mockGetTraderKnownFactsService) {
    def callTransform[A](request: MovementRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" - {

    "when there is no data in the cache" - {

      "must set userAnswers to 'None' in the request" in {

        MockUserAnswersService.get(testErn, testArc).returns(Future.successful(None))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.callTransform(
          MovementRequest(UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false), testArc, getMovementResponseModel)
        ).futureValue

        result.userAnswers must not be defined
      }
    }

    "when there is data in the cache" - {

      "must build a userAnswers object and add it to the request" in {

        MockUserAnswersService.get(testErn, testArc).returns(Future(Some(emptyUserAnswers)))
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(Some(testMinTraderKnownFacts)))

        val result = dataRetrievalAction.callTransform(
          MovementRequest(UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false), testArc, getMovementResponseModel)
        ).futureValue

        result.userAnswers mustBe defined
      }
    }
  }
}
