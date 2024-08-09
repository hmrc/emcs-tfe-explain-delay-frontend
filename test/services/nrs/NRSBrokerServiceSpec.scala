/*
 * Copyright 2024 HM Revenue & Customs
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

package services.nrs

import base.SpecBase
import fixtures.{NRSBrokerFixtures, SubmitExplainDelayFixtures}
import mocks.connectors.{MockAuthConnector, MockNRSBrokerConnector}
import models.nrs.ExplainDelayNRSSubmission
import models.requests.DataRequest
import models.{IdentityDataException, UnexpectedDownstreamResponseError}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.nrs.NRSBrokerService.retrievals
import uk.gov.hmrc.auth.core.UnsupportedAuthProvider
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}
import utils.TimeMachine

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class NRSBrokerServiceSpec extends SpecBase
  with MockNRSBrokerConnector
  with MockAuthConnector
  with NRSBrokerFixtures
  with SubmitExplainDelayFixtures {

  implicit lazy val ec: ExecutionContext = ExecutionContext.global
  implicit lazy val req: DataRequest[_] = dataRequest(FakeRequest())

  val instantNow: Instant = Instant.now()
  private val timeMachine: TimeMachine = () => Instant.ofEpochMilli(1L)

  implicit lazy val headerCarrierWithAuthToken: HeaderCarrier = HeaderCarrier().copy(authorization = Some(Authorization(testAuthToken)))
  lazy val service = new NRSBrokerService(mockNRSBrokerConnector, mockAuthConnector, timeMachine)

  ".submitPayload" - {

    "should return a Left" - {

      "when retrieving the identity data throws an exception" in {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.failed(UnsupportedAuthProvider("Game over!")))

        await(service.submitPayload(explainDelayModel, testErn)) mustBe Left(IdentityDataException("UnsupportedAuthProvider"))
      }

      "when the broker connector returns a Left" in {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.successful(predicateRetrieval))
        MockNRSBrokerConnector.submitPayload(createNRSPayload(ExplainDelayNRSSubmission(testErn, explainDelayModel)), testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        await(service.submitPayload(explainDelayModel, testErn)) mustBe Left(UnexpectedDownstreamResponseError)
      }

      "when the broker connector returns an unhandled future exception" in {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.successful(predicateRetrieval))
        MockNRSBrokerConnector.submitPayload(createNRSPayload(ExplainDelayNRSSubmission(testErn, explainDelayModel)), testErn).returns(Future.failed(new Exception("Game over!")))

        await(service.submitPayload(explainDelayModel, testErn)) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "should return a Right" - {

      "when the identity data is retrieved successfully and the payload is submitted to the broker correctly" in {

        MockAuthConnector.authorise(EmptyPredicate, retrievals).returns(Future.successful(predicateRetrieval))
        MockNRSBrokerConnector.submitPayload(createNRSPayload(ExplainDelayNRSSubmission(testErn, explainDelayModel)), testErn).returns(Future.successful(Right(nrsBrokerResponseModel)))

        await(service.submitPayload(explainDelayModel, testErn)) mustBe Right(nrsBrokerResponseModel)
      }
    }
  }
}
